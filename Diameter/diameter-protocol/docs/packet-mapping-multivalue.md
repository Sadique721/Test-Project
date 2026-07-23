# Packet Mapping — Multi-instance (repeated) grouped AVPs

## Purpose
Send the **same grouped AVP more than once** in one answer/packet — e.g. two
`Charging-Rule-Install → Charging-Rule-Definition (1003)` rules, or several
`Multiple-Services-Credit-Control (456)` blocks — driven entirely from the
Diameter packet-mapping configuration (the `diameter-packet` admin screen /
`mapping_detail` rows). No code change is needed to add another instance; only
configuration.

## The convention
In the response-AVP path of a mapping detail, prefix a path segment with
`<n>_` (an instance index and an underscore) to create a distinct instance of
that grouped AVP. The prefix only affects grouping; it is **stripped** before
the dictionary lookup and before the bytes go on the wire, so the emitted AVP
code is the real one.

```
Rule 1 (unchanged, no prefix):     1001.1003.1005      = Charging-Rule-Name
                                   1001.1003.432       = Rating-Group
                                   1001.1003.439       = Service-Identifier
                                   1001.1003.1010      = Precedence

Rule 2 (new sibling definition):   1001.2_1003.1005
                                   1001.2_1003.432
                                   1001.2_1003.439
                                   1001.2_1003.1010
```

Both rules share the single `Charging-Rule-Install (1001)` parent and appear as
two separate `Charging-Rule-Definition (1003)` children. Add a third rule with
`3_1003`, and so on.

## Why existing configs are unaffected
- A segment **without** a `<n>_` prefix is passed through unchanged
  (`replaceFirst("^\\d+_", "")` is a no-op), so the cache key, dictionary
  lookup and emitted bytes are byte-for-byte identical to before.
- The instance prefix is part of the internal grouping cache key only; it never
  reaches the wire.
- This generalises the previous hard-coded `1_`/`2_` handling (which already
  existed in `DiameterUtils.addNestedAvp`) to any instance count, keeping the
  old two-instance configs working exactly as before.

## Where it is implemented
- `com.diameter.commons.DiameterUtils#addNestedAvp` — used by Ro and Rx.
- `com.diameter.handler.ServerGxCCRHandler#addNestedAvp` — Gx.
- `com.diameter.handler.ServerGyCCRHandler#addNestedAvp` — Gy.
- Gx grouped-AVP flush now recomputes length (`refreshAVPHeader()`), matching
  Gy and `DiameterUtils`, so an enlarged parent (multiple children) encodes
  correctly.

## Values per instance
Each instance's leaf values come from its own mapping-detail row, so **literal**
values (e.g. `Service-Identifier = 2001`, `Charging-Rule-Name = Daily_10MB_SavAPP`,
`Rating-Group = 2001`, `Precedence = 1`) are fully supported today.

Limitation: a `${...}` value expression resolves against a single per-request
value map (one value per key), so two instances cannot yet derive *different*
computed values from the same key. Static/literal rule catalogues are the
supported case; per-instance dynamic values would need an indexed value key and
are out of scope for this change.

## Admin UI note
The response-AVP field on the `diameter-packet` screen must accept the `<n>_`
prefix (e.g. `2_1003`). If that field enforces a numeric/format validation,
it must be relaxed to allow the prefix.
