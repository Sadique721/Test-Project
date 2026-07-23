export interface Policy {
    id?: number;
    name: string;
    description: string;
    thPolicyName: string;
    baseParam1?: string;
    baseParam2?: string;
    baseParam3?: string;
    thParam1?: string;
    thParam2?: string;
    thParam3?: string;
    isDeleted?: number;
    createdByStaffid?: number;
    createDate?: string;
    lastModifiedByStaffId?: number;
    lastModifiedDate?: string;
    basePolicyName: string;
    createByName?: string;
    updateByName?: string;
    mvnoid: number;
    type?: string;
    qosSpeed: string;
    upstreamProfileUID?: number;
    downstreamProfileUID?: number;
}

export interface PolicyResponse {
    responseCode?: number;
    responseMessage?: string;
    data?: Policy | Policy[];
}

export interface PolicySearchParams {
    name?: string;
    type?: string;
    mvnoid?: number;
    page?: number;
    size?: number;
}