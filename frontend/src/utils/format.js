export function truncate(text, max = 40) {
    if (!text) return "-";
    if (text.length <= max) return text;
    return `${text.slice(0, max)}...`;
}
