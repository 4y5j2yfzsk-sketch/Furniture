package com.furniture.designer.data

data class Cabinet(
    val width: Int,
    val height: Int,
    val depth: Int,
    val thickness: Int = 16,
    val shelves: Int = 3,
    val backThickness: Int = 3,
    val doors: Int = 2,
    val edgeBanding: Int = 2
)

data class FurniturePart(
    val name: String,
    val width: Int,
    val height: Int,
    val quantity: Int = 1,
    val material: String = "ЛДСП 16 мм",
    val edge: String = "По умолчанию"
)

data class CuttingSheet(
    val width: Int = 2800,
    val height: Int = 2070
)

data class PlacedPart(
    val part: FurniturePart,
    val x: Int,
    val y: Int,
    val rotated: Boolean,
    val placedWidth: Int,
    val placedHeight: Int
)

data class CuttingResult(
    val sheets: List<List<PlacedPart>>,
    val unplaced: List<FurniturePart>,
    val sheet: CuttingSheet
)
