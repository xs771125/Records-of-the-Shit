package com.shiji.app.domain.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val rank: String,          // 爵位/官职名
    val condition: String,     // human-readable unlock condition
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0
)

object Achievements {
    val ALL = listOf(
        Achievement(
            id = "first_record",
            title = "初秉史笔",
            description = "记下第一条史录",
            emoji = "✍️",
            rank = "史官",
            condition = "完成首次记录"
        ),
        Achievement(
            id = "streak_3",
            title = "三日不绝",
            description = "连续三天记录",
            emoji = "📜",
            rank = "修撰",
            condition = "连续记录3天"
        ),
        Achievement(
            id = "streak_7",
            title = "七日连击",
            description = "连续七天记录",
            emoji = "🔥",
            rank = "编修",
            condition = "连续记录7天"
        ),
        Achievement(
            id = "streak_14",
            title = "二旬之约",
            description = "连续十四天记录",
            emoji = "⚡",
            rank = "侍讲学士",
            condition = "连续记录14天"
        ),
        Achievement(
            id = "streak_30",
            title = "一月贯之",
            description = "连续三十天记录",
            emoji = "👑",
            rank = "大学士",
            condition = "连续记录30天"
        ),
        Achievement(
            id = "total_10",
            title = "十录成篇",
            description = "累计记录十条",
            emoji = "📖",
            rank = "起居注",
            condition = "累计记录10条"
        ),
        Achievement(
            id = "total_50",
            title = "五十之数",
            description = "累计记录五十条",
            emoji = "📚",
            rank = "著作郎",
            condition = "累计记录50条"
        ),
        Achievement(
            id = "total_100",
            title = "百录成史",
            description = "累计记录一百条",
            emoji = "🏆",
            rank = "太史令",
            condition = "累计记录100条"
        ),
        Achievement(
            id = "bristol_4",
            title = "完美之形",
            description = "记录到一次Bristol 4型",
            emoji = "🍌",
            rank = "形神兼备",
            condition = "记录Bristol 4型（完美型）"
        ),
        Achievement(
            id = "early_bird",
            title = "寅时之臣",
            description = "在凌晨4-6点记录",
            emoji = "🌅",
            rank = "侍读学士",
            condition = "在寅时（4:00-6:00）记录"
        ),
        Achievement(
            id = "all_bristol",
            title = "七形毕现",
            description = "集齐七种Bristol形态",
            emoji = "🌈",
            rank = "博学鸿儒",
            condition = "记录过全部7种Bristol类型"
        ),
        Achievement(
            id = "all_colors",
            title = "五色斑斓",
            description = "集齐所有颜色",
            emoji = "🎨",
            rank = "丹青妙手",
            condition = "记录过全部7种颜色"
        ),
        Achievement(
            id = "triple_day",
            title = "一日三记",
            description = "一天之内记录三次",
            emoji = "💩",
            rank = "勤于笔耕",
            condition = "同一天记录3次及以上"
        ),
        Achievement(
            id = "golden_hour",
            title = "黄金守时",
            description = "在黄金时段记录10次以上",
            emoji = "⏰",
            rank = "司辰",
            condition = "同一时段记录超过10次"
        ),
        Achievement(
            id = "long_interval",
            title = "不屈不挠",
            description = "间隔超过7天后又回来记录",
            emoji = "💪",
            rank = "卷土重来",
            condition = "中断7天后再次记录"
        )
    )
}
