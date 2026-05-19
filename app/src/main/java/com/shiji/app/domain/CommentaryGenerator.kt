package com.shiji.app.domain

import com.shiji.app.data.entity.Record
import java.util.Calendar
import kotlin.random.Random

object CommentaryGenerator {

    private val timePhrases = mapOf(
        "early_dawn" to listOf(
            "闻鸡起坐", "鸡鸣即起", "天未明而事已成", "晨光熹微之际",
            "寅时惊梦，披衣急趋", "东方既白，腹中雷鸣", "五更天未曙，恭桶已候驾",
            "晨鸟未啼，人先起"
        ),
        "morning" to listOf(
            "晨起之作", "朝食之后", "旭日东升时", "晨间例事",
            "朝阳初上，五谷轮回", "早膳方罢，肠腑催行", "一日之计在于晨，一晨之重于厕",
            "辰时吉，宜出恭", "朝露未干，事已毕"
        ),
        "late_morning" to listOf(
            "日上三竿", "巳时之便", "临近午时", "半日将尽",
            "午前一阵雷鸣", "将午未午，腹有异动", "半日劳顿，先清后食"
        ),
        "afternoon" to listOf(
            "午后从容", "日昳之时", "午后闲暇", "日影西斜际",
            "下午茶后，畅然有之", "日过中天，浊气下沉", "未时之约，如期而至",
            "午后小憩毕，神清气爽", "斜阳入户，正是良辰"
        ),
        "evening" to listOf(
            "晚间功课", "日暮而归", "华灯初上时", "黄昏之际",
            "薄暮冥冥，腹中翻涌", "晚饭之后，例行公事", "灯火阑珊处，一人独坐",
            "戌时到，功课不可废", "暮色四合，一泻了之"
        ),
        "night" to listOf(
            "夜深人静", "夤夜独坐", "万籁俱寂时", "更深人未眠",
            "夜半腹中鼓角起", "三更无眠，唯此事可待", "星垂平野，月涌恭桶",
            "夜色深沉，独享清静时"
        ),
        "midnight" to listOf(
            "夜半更深", "三更之事", "子时之约", "半夜惊坐起",
            "子夜腹鸣如鼓", "夜深千帐灯，独向茅厕行", "半夜三更，急如星火",
            "梦中惊醒，汗出如浆"
        )
    )

    private val bristolPhrases = mapOf(
        1 to listOf(
            "硬如羊矢，虽艰涩终成之", "粒粒分明，勉力而为", "坚如磐石，非一日之功",
            "如投石入水，砰然有声", "似铁丸落地，铿锵作响", "干如久旱之地，裂如龟甲",
            "一别三日，硬核相见"
        ),
        2 to listOf(
            "凹凸有致，终成气候", "虽多疙瘩，不掩其志", "结实成团，尚需努力",
            "如葡萄之串，颗颗分明", "外貌虽陋，其心实坚", "疙疙瘩瘩，亦是风景"
        ),
        3 to listOf(
            "稍有裂痕，大致成形", "形似香肠，略有瑕疵", "虽有不足，已然不错",
            "近乎完美，差之毫厘", "瑕不掩瑜，大体可观", "三分形似，七分神似"
        ),
        4 to listOf(
            "形神兼备，上品也", "完美之形，无可挑剔", "蛇形柔滑，如丝如缎",
            "此物只应天上有", "浑然天成，鬼斧神工", "如丝绸滑过，不滞不涩",
            "教科书级别的典范", "太史公见之亦颔首", "此形一出，天下无屎"
        ),
        5 to listOf(
            "柔软易出，边界清晰", "软而不散，自有章法", "绵软适中，甚合朕意",
            "虽软犹有骨，虽散尚有形", "柔中带刚，软而有度", "四散开来，各自为政"
        ),
        6 to listOf(
            "散漫无形，其势如絮", "蓬松糊状，或受风寒", "边缘破烂，然其质可嘉",
            "如云如雾，不可捉摸", "烂而不泄，尚有底线", "糊状而出，如释重负",
            "溏而不泄，幸甚至哉"
        ),
        7 to listOf(
            "其势如江河决堤", "一泻千里，痛快淋漓", "水银泻地，势不可挡",
            "奔腾不息，须臾之间", "如庐山瀑布，飞流直下", "大江东去浪淘尽",
            "黄河之水天上来，奔流到海不复回", "虽疾风骤雨，终有竟时",
            "酣畅淋漓，天下武功唯快不破"
        )
    )

    private val smoothnessPhrases = mapOf(
        "high" to listOf(
            "顺畅如飞，甚善", "一蹴而就，爽利非凡", "如行云流水",
            "势如破竹，一气呵成", "不费吹灰之力", "如丝般顺滑",
            "一滑到底，酣畅无比", "顺水推舟，自然天成", "如刀切豆腐，干净利落",
            "仿佛德芙，尽享丝滑"
        ),
        "medium" to listOf(
            "尚算顺畅，无事", "中规中矩，足以自慰", "无惊无险，平安是福",
            "既非坦途，亦非险阻", "平平淡淡才是真", "不紧不慢，恰到好处"
        ),
        "low" to listOf(
            "历尽艰辛，可歌可泣", "虽费力甚巨，终见成效", "千呼万唤始出来",
            "一波三折，终成正果", "艰难困苦，玉汝于成", "面红耳赤，汗出如浆",
            "费九牛二虎之力", "如大禹治水，三过肛门而不入",
            "使出洪荒之力", "其难产之状，可入列传"
        )
    )

    private val odorPhrases = mapOf(
        "high" to listOf(
            "气味厚重，可避蚊虫", "余韵绕梁，三日不绝", "气味非凡，唯快出为妙",
            "满室生香，非寻常可比", "其味可通天地，感鬼神",
            "屏息而退，三舍之外方可呼吸", "鼻观之勇，不下上阵杀敌",
            "气味冲霄汉，蚊蝇俱欢颜"
        ),
        "medium" to listOf(
            "气味适中，不惊不扰", "略有异味，人之常情",
            "不卑不亢，中正平和", "气味平常，不值一提"
        ),
        "low" to listOf(
            "气味清雅，如沐春风", "淡如幽兰，几不可闻", "清雅脱俗，实属难得",
            "几无气味，堪称奇迹", "清风过处，不留痕迹", "淡雅如菊，实属罕见"
        )
    )

    private val streakPhrases = listOf(
        "连七日不辍，真乃奇人也",
        "日日坚持，史官之楷模",
        "连日记录，可见恒心",
        "此等毅力，宜封「坚持侯」",
        "恒心似铁，堪比太史公著史",
        "七日之约，雷打不动",
        "连记不绝，屎家之曾子也"
    )

    private val bonusPhrases = listOf(
        "可记入本纪，传之后世。",
        "史官曰：善。",
        "堪称一时佳话。",
        "此事当载入史册。",
        "诚可谓屎家之绝唱。",
        "当浮一大白。",
        "真乃天选之肛门也。",
        "可为后世法。",
        "可入屎记·列传。",
        "太史公搁笔叹曰：妙哉。",
        "此等事迹，宜勒石以记之。",
        "当配享太庙，受千古膜拜。",
        "子不语怪力乱屎，然此事不得不记。",
        "此条当加密归档，非礼勿视。"
    )

    // Classical allusions and jokes
    private val classicalAllusions = listOf(
        "昔者越王勾践卧薪尝胆，今有阁下卧厕尝屎，其志一也。",
        "《诗经》云：「关关雎鸠，在河之洲。」今有阁下蹲于恭桶之上，亦是一景。",
        "孔子曰：「三人行，必有我师焉。」阁下独行于厕，亦可为师。",
        "老子云：「道可道，非常道。屎可屎，非常屎。」",
        "孙子曰：「知己知彼，百战不殆。」阁下知己之屎，可谓知彼矣。",
        "韩非子云：「刑过不避大臣，赏善不遗匹夫。」阁下之屎，不避任何人。",
        "庄周梦蝶，不知周之梦为蝶与，蝶之梦为周与。阁下蹲厕，不知屎之拉人，人之拉屎。",
        "曹操曰：「宁可我负天下人，休教天下人负我。」阁下曰：「宁可屎负我，休教我负屎。」"
    )

    fun generate(record: Record, isStreak: Boolean = false): String {
        val cal = Calendar.getInstance().apply { timeInMillis = record.timestamp }
        val hour = cal.get(Calendar.HOUR_OF_DAY)

        val fallbackPhrases = listOf("今日之事", "细思有得", "其一端也")

        val timePhrase = pick(timePhrases[when (hour) {
            in 4..6 -> "early_dawn"
            in 7..9 -> "morning"
            in 10..11 -> "late_morning"
            in 12..17 -> "afternoon"
            in 18..21 -> "evening"
            in 22..23 -> "night"
            else -> "midnight"
        }] ?: fallbackPhrases)

        val bristolPhrase = pick(bristolPhrases[record.bristolType] ?: bristolPhrases[4] ?: fallbackPhrases)

        val smoothPhrase = pick(smoothnessPhrases[when {
            record.smoothness >= 4 -> "high"
            record.smoothness == 3 -> "medium"
            else -> "low"
        }] ?: fallbackPhrases)

        val odorPhrase = pick(odorPhrases[when {
            record.odorLevel >= 4 -> "high"
            record.odorLevel == 3 -> "medium"
            else -> "low"
        }] ?: fallbackPhrases)

        val bonus = if (Random.nextFloat() < 0.3f) pick(bonusPhrases) else ""
        val streakComment = if (isStreak && Random.nextFloat() < 0.4f) pick(streakPhrases) else ""
        val classical = if (Random.nextFloat() < 0.12f) pick(classicalAllusions) else ""

        // Pick a template
        val template = when (Random.nextInt(5)) {
            0 -> "太史公曰：${timePhrase}，${bristolPhrase}。${smoothPhrase}，${odorPhrase}。"
            1 -> "太史公曰：今日${bristolPhrase}。${timePhrase}而作，${smoothPhrase}。"
            2 -> "太史公曰：${timePhrase}，即${smoothPhrase}。观其形，${bristolPhrase}。闻其味，${odorPhrase}。"
            3 -> "太史公曰：${bristolPhrase}，${smoothPhrase}。${timePhrase}之事，${odorPhrase}。"
            4 -> "太史公按：${timePhrase}有记。${bristolPhrase}，${smoothPhrase}，${odorPhrase}。"
            else -> "太史公曰：${timePhrase}，${bristolPhrase}。${smoothPhrase}，${odorPhrase}。"
        }

        val parts = mutableListOf<String>()
        parts.add(template)
        if (streakComment.isNotBlank()) parts.add(streakComment)
        if (bonus.isNotBlank()) parts.add(bonus)
        if (classical.isNotBlank()) parts.add(classical)

        return parts.joinToString("")
    }

    private fun pick(list: List<String>): String {
        val index = (Random.nextInt(list.size))
        return list[index]
    }
}
