package com.shiji.app.domain.model

import androidx.compose.ui.graphics.Color

enum class BristolType(
    val value: Int,
    val emoji: String,
    val label: String,
    val description: String,
    val displayColor: Color
) {
    TYPE_1(1, "🪨", "分离硬块", "似羊矢，颗粒分明", Color(0xFF8B6914)),
    TYPE_2(2, "🥖", "香肠多疙瘩", "凹凸有致，结实成团", Color(0xFFA67C3A)),
    TYPE_3(3, "🌭", "香肠有裂痕", "稍有裂痕，大致成形", Color(0xFFBF8F4A)),
    TYPE_4(4, "🍌", "香肠蛇状光滑", "形神兼备，完美之形", Color(0xFF8FBC8F)),
    TYPE_5(5, "🫘", "软块边缘光滑", "柔软易出，边界清晰", Color(0xFFBDB76B)),
    TYPE_6(6, "☁️", "蓬松糊状", "散漫无形，边缘破烂", Color(0xFFCD853F)),
    TYPE_7(7, "💧", "水状无固体", "奔腾不息，一泻千里", Color(0xFF87CEEB));

    companion object {
        fun fromValue(value: Int): BristolType =
            entries.firstOrNull { it.value == value } ?: TYPE_4
    }
}
