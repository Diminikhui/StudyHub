function solveQuadratic(a, b, c) {
    const d = b ** 2 - 4 * a * c;
    if (d < 0) return [];
    if (d === 0) return [-b / (2 * a)];
    const sqrtD = Math.sqrt(d);
    return [(-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a)];
}