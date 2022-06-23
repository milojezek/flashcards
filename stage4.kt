package flashcards

val cards = mutableMapOf<String, String>()

fun main() {
    println("Input the number of cards:")
    createCards(readln().toInt())
    askUserForDefinitions()
}

fun createCards(numberOfCards: Int) {
    var cardNumber = 1
    while (cardNumber <= numberOfCards) {
        var termNeeded = true
        lateinit var term: String
        println("Card #$cardNumber:")

        // Checks if term is correct
        while (termNeeded) {
            term = readln()
            if (term in cards.keys) {
                println("The term \"$term\" already exists. Try again:")
                continue
            } else {
                termNeeded = false
            }
        }
        
        var definitionNeeded = true
        println("The definition for card #$cardNumber:")
        
        // Checks if definition is correct
        while (definitionNeeded) {
            val definition = readln()
            if (definition in cards.values) {
                println("The definition \"$definition\" already exists. Try again:")
                continue
            } else {
                cards[term] = definition
                definitionNeeded = false
                cardNumber++
            }
        }
    }
}

fun askUserForDefinitions() {
    for (term in cards.keys) {
        println("Print the definition of \"$term\":")
        val answer = readln()
        val correctAnswer = cards[term]
        println(
            when (answer) {
                correctAnswer -> "Correct!"
                in cards.values -> "Wrong. The right answer is \"$correctAnswer\", " +
                        "but your definition is correct for " +
                        "\"${getKey(cards, answer)}\"."
                else -> "Wrong. The right answer is \"$correctAnswer\"."
            }
        )
    }
}

fun getKey(map: Map<String, String>, target: String?): String? {
    for ((key, value) in map) {
        if (value == target) {
            return key
        }
    }
    return null
}
