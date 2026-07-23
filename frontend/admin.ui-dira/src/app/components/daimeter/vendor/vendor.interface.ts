export interface Vendor {
    vendor_id?: number;
    name: string;
    description: string;
    status: 'ACTIVE' | 'INACTIVE' | 'PENDING';
}

export interface VendorResponse {
    responseCode?: number;
    responseMessage?: string;
    data?: Vendor | Vendor[];
}

export interface VendorSearchParams {
    name?: string;
    status?: string;
    page?: number;
    size?: number;
}