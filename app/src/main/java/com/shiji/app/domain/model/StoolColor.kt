package com.shiji.app.domain.model

import androidx.compose.ui.graphics.Color

enum class StoolColor(
    val value: String,
    val label: String,
    val displayColor: Color,
    val description: String
) {
    BROWN("棕色", "棕色", Color(0xFF8B6914), "五谷调和之色，正常"),
    DARK_BROWN("深褐色", "深褐", Color(0xFF5C4033), "稍深，无碍"),
    BLACK("黑色", "黑色", Color(0xFF2F2F2F), "如墨似炭，留意饮食"),
    GREEN("绿色", "绿色", Color(0xFF4A7C59), "青翠之色，或食菜多"),
    YELLOW("黄色", "黄色", Color(0xFFD4A843), "金黄耀眼，脾胃有异"),
    PALE("灰白色", "灰白", Color(0xFFC4BFAF), "素白无华，宜加留意"),
    RED("红色/血色", "血色", Color(0xFF8B2500), "丹朱之色，速就医");

    companion object {
        fun fromValue(value: String): StoolColor =
            entries.firstOrNull { it.value == value } ?: BROWN
    }
}
