package net.lz1998.zbot.enums


enum class TNoodle(
        val instruction: String,
        val shortName: String,
        val showName: String) {
    PUZZLE_222("二", "222", "2阶"),
    PUZZLE_333("三", "333", "3阶"),
    PUZZLE_444("四", "444", "4阶"),
    PUZZLE_555("五", "555", "5阶"),
    PUZZLE_666("六", "666", "6阶"),
    PUZZLE_777("七", "777", "7阶"),
    PUZZLE_333FM("少", "333fm", "333fm")
}