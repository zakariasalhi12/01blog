import { BackedURL } from "../../environments/environment";

export function getFullFileUrl(fileUrl: string | undefined): string | null {
    if (!fileUrl) return null;
    return `${BackedURL}${fileUrl}`;
  }