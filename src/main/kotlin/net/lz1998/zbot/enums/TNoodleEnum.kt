package net.lz1998.zbot.enums


enum class TNoodleEnum(
        val instruction: String,
        val shortName: String,
        val showName: String) {
    PUZZLE_222("2", "222", "2阶"),
    PUZZLE_333("3", "333", "3阶"),
    PUZZLE_444("4", "444", "4阶"),
    PUZZLE_555("5", "555", "5阶"),
    PUZZLE_666("6", "666", "6阶"),
    PUZZLE_777("7", "777", "7阶"),
    PUZZLE_888("8", "888", "8阶"),
    PUZZLE_999("9", "999", "9阶"),
    PUZZLE_101010("10", "101010", "10阶"),
    PUZZLE_111111("11", "111111", "11阶"),
    PUZZLE_121212("12", "121212", "12阶"),
    PUZZLE_131313("13", "131313", "13阶"),
    PUZZLE_141414("14", "141414", "14阶"),
    PUZZLE_151515("15", "151515", "15阶"),
    PUZZLE_161616("16", "161616", "16阶"),
    PUZZLE_171717("17", "171717", "17阶"),
    PUZZLE_PYRAM("py", "pyram", "pyram"),
    PUZZLE_SKEWB("sk", "skewb", "skewb"),
    PUZZLE_SQ1("sq", "sq1", "sq1"),
    PUZZLE_CLOCK("cl", "clock", "clock"),
    PUZZLE_MINX("mx", "minx", "minx"),
    PUZZLE_333FM("fm", "333fm", "333fm")
}