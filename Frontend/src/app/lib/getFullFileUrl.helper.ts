import { BackedURL } from "../../environments/environment";

export function getFullFileUrl(fileUrl: string | undefined): string | undefined {
    if (!fileUrl) return undefined;
    return `${BackedURL}${fileUrl}`;
  }