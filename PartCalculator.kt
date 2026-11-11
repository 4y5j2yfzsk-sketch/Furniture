package com.furniture.designer.engine

import com.furniture.designer.data.Cabinet
import com.furniture.designer.data.FurniturePart

object PartCalculator {
    fun calculate(cabinet: Cabinet): List<FurniturePart> {
        require(cabinet.width in 100..10000) { "Ширина должна быть от 100 до 10000 мм" }
        require(cabinet.height in 100..10000) { "Высота должна быть от 100 до 10000 мм" }
        require(cabinet.depth in 100..3000) { "Глубина должна быть от 100 до 3000 мм" }
        require(cabinet.thickness in 8..40) { "Толщина плиты должна быть от 8 до 40 мм" }
        require(cabinet.shelves in 0..50) { "Количество полок должно быть от 0 до 50" }
        require(cabinet.doors in 0..6) { "Количество дверей должно быть от 0 до 6" }
        require(cabinet.edgeBanding in 0..5) { "Толщина кромки должна быть от 0 до 5 мм" }
        require(cabinet.width > 2 * cabinet.thickness) { "Ширина корпуса слишком мала для выбранной толщины" }

        val innerWidth = cabinet.width - 2 * cabinet.thickness
        val doorGap = 2
        val parts = mutableListOf<FurniturePart>()

        parts += FurniturePart("Боковина", cabinet.depth, cabinet.height, 2,
            "ЛДСП ${cabinet.thickness} мм", "Передняя кромка")
        parts += FurniturePart("Верхняя панель", innerWidth, cabinet.depth, 1,
            "ЛДСП ${cabinet.thickness} мм", "Передняя кромка")
        parts += FurniturePart("Нижняя панель", innerWidth, cabinet.depth, 1,
            "ЛДСП ${cabinet.thickness} мм", "Передняя кромка")

        if (cabinet.shelves > 0) {
            parts += FurniturePart("Полка", innerWidth, cabinet.depth, cabinet.shelves,
                "ЛДСП ${cabinet.thickness} мм", "Передняя кромка")
        }

        parts += FurniturePart("Задняя стенка", cabinet.width, cabinet.height, 1,
            "ХДФ ${cabinet.backThickness} мм", "Без кромки")

        if (cabinet.doors > 0) {
            val doorWidth = (cabinet.width - (cabinet.doors + 1) * doorGap) / cabinet.doors
            val doorHeight = cabinet.height - 4
            require(doorWidth > 0 && doorHeight > 0) { "Невозможно рассчитать фасады" }
            parts += FurniturePart("Фасад", doorWidth, doorHeight, cabinet.doors,
                "ЛДСП ${cabinet.thickness} мм", "По периметру")
        }

        return parts
    }
}
