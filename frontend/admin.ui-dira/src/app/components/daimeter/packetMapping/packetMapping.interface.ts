export interface PacketMappingDetail {
    requestAvp?: string;
    responseAvp?: string;
    valueExpression: string;
    valueType: 'STRING' | 'INTEGER' | 'UNSIGNED32' | 'FLOAT' | 'GROUPED';
    sequence: number;
    mandatory: boolean;
}

export interface PacketMapping {
    id?: number;
    requestType: string;
    responseType: string;
    application: string;
    vendorId: number;
    description: string;
    enabled: boolean;
    details: PacketMappingDetail[];
    createdDate?: string;
    lastModifiedDate?: string;
    createdBy?: string;
    modifiedBy?: string;
}

export interface PacketMappingResponse {
    responseCode?: number;
    responseMessage?: string;
    data?: PacketMapping | PacketMapping[];
}

export interface PacketMappingSearchParams {
    requestType?: string;
    application?: string;
    vendorId?: number;
    enabled?: boolean;
    page?: number;
    size?: number;
}