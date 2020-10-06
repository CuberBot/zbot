package net.lz1998.zbot.enums

enum class SlidysimEnum(
        val instruction: String,
        val n: Int,
        val showName: String) {
    PUZZLE_3PUZZLE("3p", 2, "3Puzzle"),
    PUZZLE_8PUZZLE("8p", 3, "8Puzzle"),
    PUZZLE_15PUZZLE("15p", 4, "15Puzzle"),
    PUZZLE_24PUZZLE("24p", 5, "24Puzzle"),
    PUZZLE_35PUZZLE("35p", 6, "35Puzzle"),
    PUZZLE_48PUZZLE("48p", 7, "48Puzzle");
}