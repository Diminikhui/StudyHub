function Rectangle(width, height) {
    this.width = width;
    this.height = height;
    return {
        area: width * height,
        perimeter: 2 * (width + height)
    };
}