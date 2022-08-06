package flashcards

import java.io.File

var programIsRunning = true


fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        var i = 0
        do {
            when {
                args[i] == "-import" && !Flashcards.importCmd -> {
                    if (i < args.lastIndex) Flashcards.importCardsViaCmd(args[++i])
                    Flashcards.importCmd = true
                }
                args[i] == "-export" && !Flashcards.exportCmd -> {
                    if (i < args.lastIndex) Flashcards.fileForCmdExport = args[++i]
                    Flashcards.exportCmd = true
                }
            }
            i++
        } while (i <= args.lastIndex)
    }

    Flashcards.printText("${Flashcards.cardsImportedViaCmd} cards have been loaded.")


    while (programIsRunning) {
        Flashcards.allTerms = Flashcards.cards.map { it.term }
        Flashcards.allDefinitions = Flashcards.cards.map { it.definition }
        Flashcards.cards.map { it.alreadyAsked = false }
        Flashcards.selectAction()
    }
}


object Flashcards {
    data class Card(val term: String, val definition: String, var wrongAnswers: Int = 0, var alreadyAsked: Boolean = false)
    val cards = mutableListOf<Card>()
    private val logs = mutableListOf("LOGS")

    // Auxiliary variables
    lateinit var allTerms: List<String>
    lateinit var allDefinitions: List<String>
    var importCmd = false
    var exportCmd = false
    var cardsImportedViaCmd = 0
    private var cardsExportedViaCmd = 0
    var fileForCmdExport: String = ""

    fun selectAction() {
        printText("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readText()) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> importCards()
            "export" -> exportCards()
            "ask" -> chooseQuestion()
            "log" -> saveLog()
            "hardest card" -> getHardestCard()
            "reset stats" -> resetStats()
            "exit" -> {
                printText("Bye bye!")
                if (fileForCmdExport.isNotEmpty()) {
                    exportCardsViaCmd(fileForCmdExport)
                    printText("$cardsExportedViaCmd cards have been saved.")
                }
                programIsRunning = false
            }
        }
        printText()
    }

    private fun addCard() {
        printText("The card:")
        val term = readText()
        if (term in allTerms) {
            printText("The card \"$term\" already exists.")
            return
        } else {
            printText("The definition of the card:")
            val definition = readText()
            if (definition in allDefinitions) {
                printText("The definition \"$definition\" already exists.")
                return
            } else {
                cards.add(Card(term, definition))
                printText("The pair (\"$term\":\"$definition\") has been added.")
            }
        }
    }

    private fun removeCard() {
        printText("Which card?")
        val cardToRemove = readText()
        if (cardToRemove in allTerms) {
            cards.removeAll { it.term == cardToRemove}
            printText("The card has been removed.")
        } else {
            printText("Can't remove \"$cardToRemove\": there is no such card.")

        }
    }

    private fun importCards() {
        var cardsImported = 0
        printText("File name:")
        val file = File(readText())
        if (file.exists()) {
            val lines = file.readLines()
            for (line in lines) {
                val term = line.split(":")[0]
                val definition = line.split(":")[1]
                val wrongAnswers = line.split(":")[2].toInt()
                cards.removeAll { it.term == term }
                cards.add(Card(term, definition, wrongAnswers))
                cardsImported++
            }
            printText("$cardsImported cards have been loaded.")
        } else {
            printText("File not found.")
        }
    }

    private fun exportCards() {
        printText("File name:")
        val file = File(readText())
        val cardsExported = cards.size
        val cardsFormatted = cards.joinToString("\n")
        { "${it.term}:${it.definition}:${it.wrongAnswers}" }

        file.writeText(cardsFormatted)
        printText("$cardsExported cards have been saved.")
    }

    private fun chooseQuestion() {
        printText("How many times to ask?")
        val numberOfQuestions = readText().toInt()
        var askedQuestions = 0
        while (askedQuestions < numberOfQuestions) {
            val unansweredQuestions = cards.filter { !it.alreadyAsked }
//        val randomIndex = Random().nextInt(0, unansweredQuestions.size - 1)
            askForDefinition(unansweredQuestions[0])
            askedQuestions++
        }
    }

    private fun askForDefinition(card: Card) {
        printText("Print the definition of \"${card.term}\":")
        val answer = readText()
        val correctAnswer = card.definition
        when (answer) {
            correctAnswer -> printText("Correct!")
            in allDefinitions -> {
                printText("Wrong. The right answer is \"$correctAnswer\", " +
                        "but your definition is correct for " +
                        "\"${cards.filter { it.definition == answer }[0].term}\".")
                card.wrongAnswers++
            }
            else -> {
                printText("Wrong. The right answer is \"$correctAnswer\".")
                card.wrongAnswers++
            }
        }
        card.alreadyAsked = true
    }

    private fun saveLog() {
        printText("File name:")
        val file = File(readText())
        val logsFormatted = logs.joinToString("\n")
        file.writeText(logsFormatted)
        printText("The log has been saved.")
    }

    fun printText(text: Any = "") {
        logs.add(text.toString())
        println(text)
    }

    private fun readText(): String {
        val input = readln()
        logs.add(input)
        return input
    }

    private fun getHardestCard() {
        val wrongAnswersOfEachCard = cards.map { it.wrongAnswers }

        val wrongTop = wrongAnswersOfEachCard.maxOrNull() ?: 0

        val mostDifficultCards = cards.filter { it.wrongAnswers == wrongTop }
        if (wrongTop == 0) {
            printText("There are no cards with errors.")
        } else if (mostDifficultCards.size == 1) {
            val oneHardest = mostDifficultCards[0]
            printText("The hardest card is \"${oneHardest.term}\"." +
                    "You have ${oneHardest.wrongAnswers} errors answering it.")
        } else {
            val severalHardest = mostDifficultCards.joinToString(", ") { "\"${it.term}\"" }
            printText("The hardest cards are $severalHardest. Yo have $wrongTop errors answering them.")
        }
    }

    private fun resetStats() {
        cards.map { it.wrongAnswers = 0 }
        printText("Card statistics have been reset.")
    }

    fun importCardsViaCmd(fileName: String) {
        var cardsImported = 0
        val file = File(fileName)
        if (file.exists()) {
            val lines = file.readLines()
            for (line in lines) {
                val term = line.split(":")[0]
                val definition = line.split(":")[1]
                val wrongAnswers = line.split(":")[2].toInt()
                cards.removeAll { it.term == term }
                cards.add(Card(term, definition, wrongAnswers))
                cardsImported++
            }
        }

        cardsImportedViaCmd = cardsImported
    }

    private fun exportCardsViaCmd(fileName: String) {
        val cardsExported = cards.size
        val file = File(fileName)
        val cardsFormatted = cards.joinToString("\n")
        { "${it.term}:${it.definition}:${it.wrongAnswers}" }

        file.writeText(cardsFormatted)
        cardsExportedViaCmd = cardsExported
    }
}
