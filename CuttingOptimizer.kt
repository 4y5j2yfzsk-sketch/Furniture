package com.furniture.designer.engine

import com.furniture.designer.data.CuttingResult
import com.furniture.designer.data.CuttingSheet
import com.furniture.designer.data.FurniturePart
import com.furniture.designer.data.PlacedPart

/**
 * Практичная эвристика guillotine packing. Это предварительная карта раскроя,
 * не заменяющая проверку технологом/оператором станка.
 */
object CuttingOptimizer {
    private data class FreeRect(val x: Int, val y: Int, val width: Int, val height: Int)

    fun optimize(
        parts: List<FurniturePart>,
        sheet: CuttingSheet = CuttingSheet(),
        kerf: Int = 4,
        trim: Int = 10
    ): CuttingResult {
        require(sheet.width > 0 && sheet.height > 0)
        require(kerf >= 0 && trim >= 0)
        val usableWidth = sheet.width - 2 * trim
        val usableHeight = sheet.height - 2 * trim
        require(usableWidth > 0 && usableHeight > 0) { "Припуск обрезки слишком велик" }

        val expanded = parts.flatMap { part ->
            require(part.width > 0 && part.height > 0 && part.quantity >= 0)
            List(part.quantity) { part.copy(quantity = 1) }
        }.sortedByDescending { it.width.toLong() * it.height.toLong() }

        val placedSheets = mutableListOf<MutableList<PlacedPart>>()
        val freeBySheet = mutableListOf<MutableList<FreeRect>>()
        val unplaced = mutableListOf<FurniturePart>()

        for (part in expanded) {
            var chosenSheet = -1
            var chosenRect = -1
            var chosenRotation = false
            var bestWaste = Long.MAX_VALUE

            for (s in freeBySheet.indices) {
                for (r in freeBySheet[s].indices) {
                    val rect = freeBySheet[s][r]
                    for (rotated in listOf(false, true)) {
                        val pw = if (rotated) part.height else part.width
                        val ph = if (rotated) part.width else part.height
                        val needW = pw + kerf
                        val needH = ph + kerf
                        if (needW <= rect.width && needH <= rect.height) {
                            val waste = rect.width.toLong() * rect.height -
                                needW.toLong() * needH
                            if (waste < bestWaste) {
                                bestWaste = waste
                                chosenSheet = s
                                chosenRect = r
                                chosenRotation = rotated
                            }
                        }
                    }
                }
            }

            if (chosenSheet == -1) {
                val fitsNormal = part.width + kerf <= usableWidth &&
                    part.height + kerf <= usableHeight
                val fitsRotated = part.height + kerf <= usableWidth &&
                    part.width + kerf <= usableHeight
                if (!fitsNormal && !fitsRotated) {
                    unplaced += part
                    continue
                }
                chosenSheet = placedSheets.size
                placedSheets += mutableListOf()
                freeBySheet += mutableListOf(FreeRect(0, 0, usableWidth, usableHeight))
                chosenRect = 0
                chosenRotation = !fitsNormal && fitsRotated
            }

            val rect = freeBySheet[chosenSheet][chosenRect]
            val pw = if (chosenRotation) part.height else part.width
            val ph = if (chosenRotation) part.width else part.height

            placedSheets[chosenSheet] += PlacedPart(
                part = part,
                x = rect.x + trim,
                y = rect.y + trim,
                rotated = chosenRotation,
                placedWidth = pw,
                placedHeight = ph
            )

            freeBySheet[chosenSheet].removeAt(chosenRect)
            val usedW = pw + kerf
            val usedH = ph + kerf
            val rightW = rect.width - usedW
            val bottomH = rect.height - usedH

            // Два непересекающихся прямоугольника после гильотинного реза.
            if (rightW > 0) {
                freeBySheet[chosenSheet] += FreeRect(
                    rect.x + usedW, rect.y, rightW, rect.height
                )
            }
            if (bottomH > 0) {
                freeBySheet[chosenSheet] += FreeRect(
                    rect.x, rect.y + usedH, usedW, bottomH
                )
            }
        }

        return CuttingResult(placedSheets, unplaced, sheet)
    }
}
