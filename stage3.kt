package flashcards

val cards = mutableMapOf<String, String>()

fun main() {
    println("Input the number of cards:")
    val numberOfCards = readln().toInt()

    for (n in 1..numberOfCards) {
        println("Card #$n:")
        val term = readln()
        println("The definition for card #$n:")
        val definition = readln()
        cards[term] = definition
    }

    for ((term, definition) in cards) {
        println("Print the definition of \"$term\":")
        val answer = readln()
        println(if (answer == cards[term]) "Correct!" else "Wrong. " +
                "The right answer is \"${cards[term]}\".")
    }
}
