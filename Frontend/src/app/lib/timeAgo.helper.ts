export function timeAgo(date: string | Date): string {
    const now = new Date();
    const past = new Date(date);
    const seconds = Math.floor((now.getTime() - past.getTime()) / 1000);
    const intervals: any = {
      year: 31536000,
      month: 2592000,
      week: 604800,
      day: 86400,
      hour: 3600,
      minute: 60,
      second: 1
    };
    for (const key in intervals) {
      const value = Math.floor(seconds / intervals[key]);
      if (value > 0) return value === 1 ? `1 ${key} ago` : `${value} ${key}s ago`;
    }
    return 'just now';
  }

