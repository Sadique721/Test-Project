# Gy Plan-Expiry RAR — Configuration Guide (post-patch)

This guide explains how to configure the **plan-expiry RAR** feature after the
patch is deployed. No further code change is required — the behaviour is driven
entirely by packet-mapping configuration.

## 1. What the feature does

When a subscriber's plan has expired (end date passed **and** the grace period
is over), the next **CCR-Update** on the Gy interface triggers a server-initiated
**Gx RAR** to the PGW/PCEF. The RAR carries a policy that you configure
separately from the FUP (throttle) RAR, so expiry can, for example, remove the
paid rule or install a blocked/zero-speed rule.

- Trigger: `!bTerminateRequest && bPlanExpire && requestType == UPDATE_REQUEST`
- Sent once per session (de-dup key `RAR-EXPIRE<sessionId>`, 1-day TTL),
  independent of any FUP RAR.
- Targets the PGW using the cached Gx session `GX-SESSION_ID<custId>`.
- The existing FUP RAR (quota-exhausted + over-usage) is unchanged.

## 2. Prerequisites

1. **Patch deployed** — build contains `com.diameter.enums.ReAuthReason` and the
   updated `ServerGyCCRHandler.sendRAR(..., ReAuthReason)`.
2. **Active Gx session cached** — the subscriber must have a live Gx session
   stored under `GX-SESSION_ID<custId>` (created on the Gx CCR path). Without it,
   the RAR falls back to the Gy requesting host/realm, which may not be the PGW.
3. **Grace-period logic** — `DiameterUtils.isGracePeriodExpired(...)` must return
   `true` for the RAR to fire (i.e. expiry is real, not within grace).

## 3. Packet-mapping configuration

Create ONE new mapping header for the expiry RAR. Leave the existing
`Re-Auth-Request` (FUP) mapping untouched.

### 3.1 Mapping header

| Field | Value |
|---|---|
| Application | `GY` |
| Request type | `Re-Auth-Request-Expiry`  (must match exactly) |
| Response type | `Re-Auth-Answer` |
| CC-Request-Type | *(empty / null)* |

> The handler selects this mapping only when the RAR reason is `PLAN_EXPIRE`
> (`ServerGyCCRHandler.sendRAR`). The label string must be exactly
> `Re-Auth-Request-Expiry`.

### 3.2 Mapping detail rows

Each row becomes an AVP in the RAR. Do **not** add Session-Id (263),
Destination-Host (293) or Destination-Realm (283) — `sendRAR` adds those itself.

Row fields: `Vendor ID`, `Response AVP` (dotted path), `Value Expression`
(literal or `${key}`), `Value Type` (`String` or `Integer`).

**Option A — remove the paid rule + re-authorize (simplest):**

| Vendor ID | Response AVP | Value Expression | Value Type | Meaning |
|---|---|---|---|---|
| 0 | `285` | `0` | Integer | Re-Auth-Request-Type = AUTHORIZE_ONLY |
| 10415 | `1002.1005` | `${customerPlan.planCode}` | String | Charging-Rule-Remove → Charging-Rule-Name |

**Option B — install a block / zero-speed rule (different values from FUP throttle):**

| Vendor ID | Response AVP | Value Expression | Value Type | Meaning |
|---|---|---|---|---|
| 0 | `285` | `0` | Integer | Re-Auth-Request-Type |
| 10415 | `1001.1003.1005` | `EXPIRED_BLOCK` | String | Charging-Rule-Install → Def → Charging-Rule-Name |
| 10415 | `1001.1003.1016.516` | `0` | Integer | QoS-Information → Max-Requested-Bandwidth-UL |
| 10415 | `1001.1003.1016.515` | `0` | Integer | QoS-Information → Max-Requested-Bandwidth-DL |
| 10415 | `1001.1003.511` | `2` | Integer | Flow-Status = DISABLED |

### 3.3 Value expressions

- **Literal** — used as-is (e.g. `EXPIRED_BLOCK`, `0`).
- **`${key}`** — resolved from the request value map. Available on the expiry
  path: `customer.*`, `customerQuota.*`, `customerPlan.*`. Note `gatewayMapping.*`
  may not be populated on pure expiry, so prefer literals for QoS values there.

### 3.4 Multiple rules in one RAR (optional)

The RAR builder supports repeated grouped AVPs via the `N_` instance prefix (see
`packet-mapping-multivalue.md`). To install/remove two rules in one expiry RAR:

```
1001.1003.1005      -> rule 1 name
1001.2_1003.1005    -> rule 2 name
...
```

## 4. Rules / constraints

- **Keep grouped-rule leaves on vendor 10415** (Charging-Rule-Name 1005,
  QoS-Information 1016, Max-Requested-Bandwidth 515/516, Flow-Status 511). The
  current `addNestedAvp` applies the row's vendor id to every non-special
  segment, so a base/vendor-0 AVP such as Rating-Group (432) inside a 10415
  group would fail the dictionary lookup. Avoid mixing base and 10415 AVPs in
  one grouped path until per-segment vendor override is added.
- The Admin UI `Response AVP` field must accept dotted paths and the `N_`
  prefix; relax any strict numeric validation if present.

## 5. Verification

1. Deploy the patch and add the `Re-Auth-Request-Expiry` mapping above.
2. For a subscriber whose plan has expired past grace, send a Gy **CCR-Update**.
3. Expected:
   - CCA on the CCR-U (existing terminate/deny behaviour, unchanged).
   - A Gx RAR to the PGW containing the AVPs you configured.
   - Log line: `ServerGyCCRHandler  Sending RAR for session: ...` and
     `RAR sent successfully`.
   - Only one RAR per session (repeat CCR-U within the TTL logs
     `Not send RAR if already sent`).
4. Confirm FUP is unaffected: a quota-exhausted + over-usage subscriber still
   gets the throttle RAR from the `Re-Auth-Request` mapping.

## 6. Rollback

- Remove or disable the `Re-Auth-Request-Expiry` mapping header → the expiry RAR
  becomes a no-op (mapping empty), FUP unaffected. No redeploy required.
