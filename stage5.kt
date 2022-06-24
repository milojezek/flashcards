package flashcards

import java.io.File
import java.util.*

val cards = mutableMapOf<String, String>()
var programIsRunning = true

fun main() {
    while (programIsRunning) {
        selectAction()
    }
}

fun selectAction() {
    println("Input the action (add, remove, import, export, ask, exit):")
    when (readln()) {
        "add" -> addCard()
        "remove" -> removeCard()
        "import" -> importCards()
        "export" -> exportCards()
        "ask" -> ask()
        "exit" -> {
            println("Bye bye!")
            programIsRunning = false
        }
    }
    println()
}

fun addCard() {
    println("The card:")
    val term = readln()
    if (term in cards.keys) {
        println("The card \"$term\" already exists.")
        return
    } else {
        println("The definition of the card:")
        val definition = readln()
        if (definition in cards.values) {
            println("The definition \"$definition\" already exists.")
            return
        } else {
            cards[term] = definition
            println("The pair (\"$term\":\"$definition\") has been added.")
        }
    }

}

fun removeCard() {
    println("Which card?")
    val cardToRemove = readln()
    if (cardToRemove in cards.keys) {
        cards.remove(cardToRemove)
        println("The card has been removed.")
    } else {
        println("Can't remove \"$cardToRemove\": there is no such card.")
    }
}

fun importCards() {
    var cardsImported = 0
    println("File name:")
    val fileName = readln()
    val file = File(fileName)
    if (file.exists()) {
        val lines = file.readLines()
        for (line in lines) {
            val term = line.split(":")[0]
            val definition = line.split(":")[1]
            cards[term] = definition
            cardsImported++
        }

        println("$cardsImported cards have been loaded.")
    } else {
        println("File not found.")
    }
}

fun exportCards() {
    var cardsExported = 0
    println("File name:")
    val file = File(readln())
    var cardsFormated = ""
    for ((term, definition) in cards) {
        cardsFormated += "$term:$definition\n"
        cardsExported++
    }

    file.writeText(cardsFormated)
    println("$cardsExported cards have been saved.")
}

fun ask() {
    println("How many times to ask?")
    val numberOfQuestions = readln().toInt()
    var askedQuestions = 0
    val alreadyAsked = mutableMapOf<String, Boolean>()
    for (term in cards.keys) {
        alreadyAsked[term] = false
    }

    while(false in alreadyAsked.values && askedQuestions < numberOfQuestions) {
        val notAskedYet = alreadyAsked.keys.filter { alreadyAsked[it] == false }
        val bound = notAskedYet.size - 1
        val randIndex = if (bound <= 0) 0 else Random().nextInt(bound)
        val chosenTerm = getKey(cards, cards[notAskedYet[randIndex]])
        askUserForDefinition(chosenTerm!!)
        alreadyAsked[chosenTerm] = true
        askedQuestions++
    }
}

fun askUserForDefinition(term: String) {
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

fun getKey(map: Map<String, String>, target: String?): String? {
    for ((key, value) in map) {
        if (value == target) {
            return key
        }
    }
    return null
}
