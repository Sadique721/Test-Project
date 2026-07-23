export interface NavItem {
    displayName?: string;
    isVisible?: boolean;
    iconName?: string;
    link?: string;
    subMenu?: NavItem[];
}