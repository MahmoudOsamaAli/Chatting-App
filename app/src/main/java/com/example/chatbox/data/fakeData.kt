package com.example.chatbox.data

class FakeData {
    data class Message(
        val name: String,
        val content: String,
        var isSilent: Boolean
    ) // Change Unit to Boolean

    fun getMessages(): List<Message> {
        val names = listOf(
            "Mahmoud", "Bob", "Hamdy", "Marwan", "Ali", "Waad", "Nada", "Donia",
            "Dina", "Tamar", "Hagar", "Sara", "Osama", "Salama", "Mostafa", "Karem",
            "DoDo", "Badr", "Twfiq", "Sasa", "Said", "Nessma", "Ahmed", "Fatma",
            "Do7a", "Hady", "Yousef", "Shahd", "Mom", "Hassan", "Joo", "Zaid",
            "Yomnna", "Mohamed", "Kareem", "Rawda", "7oda", "Alaa", "Hadi", "Dalia",
            "Tariq", "Samira", "Salem", "Hana", "Layla", "Zain", "Nour", "Hadiya",
            "Jamil", "Rami", "Ibrahim", "Khaled", "Tamer", "Hossam", "Mona", "Lina",
            "Fadya", "Yara", "Joud", "Ranya", "Dina", "Hadi", "Khalil", "Farah",
            "Omar", "Rida", "Mira", "Amira", "Anwar", "Tania", "Maha", "Sophie",
            "Nadia", "Yasmin", "Abdul", "Wissam", "Amani", "Hana", "Salma", "Maya",
            "Nadia", "Ranya", "Jasmin", "Fadi", "Hassan", "Zainab", "Saeed", "Areej",
            "Yousef", "Zara", "Faris", "Marwan", "Khadija", "Jamal", "Mohsen", "Adel",
            "Sami", "Waleed", "Safia", "Kareem", "Dania", "Nadia", "Maysaa", "Samy"
        )

        val messages = listOf(
            "How are you?",
            "Are you okay?",
            "Haha haha",
            "Good job",
            "Send the PDF.",
            "Where are you now?",
            "Do you have an Instagram account?",
            "Bye!",
            "It's okay.",
            "Fine, call me now.",
            "We need to talk right now.",
            "Send me the GitHub link.",
            "GG!",
            "How old are you?",
            "I don't know.",
            "That's a good point.",
            "Did you know?",
            "Why???",
            "Call me!",
            "Sent please.",
            "Where are you from?",
            "LOL!",
            "Let's share Spotify :3",
            "Have you seen the latest movie?",
            "What are you up to?",
            "Are you coming?",
            "Can I join you?",
            "Let’s meet up!",
            "I’m busy right now.",
            "Catch up soon?",
            "Are you free later?",
            "Let’s grab coffee!",
            "What’s new?",
            "Miss you!",
            "Let's play a game!",
            "Hope you’re doing well.",
            "Let’s catch up this weekend.",
            "How’s work going?",
            "Any weekend plans?",
            "Have you tried that new restaurant?",
            "What’s your favorite book?",
            "Are you into music?",
            "Did you hear the news?",
            "What do you think about it?",
            "What's your favorite movie?",
            "Tell me a joke!",
            "How's your family?",
            "Let’s plan a trip!",
            "Are you up for a challenge?",
            "What’s your goal this week?",
            "How's your day going?",
            "What’s your favorite hobby?",
            "Do you have any pets?",
            "What's your dream job?",
            "Are you watching any series?",
            "What’s your favorite dish?",
            "What's your travel bucket list?",
            "Let’s do something fun!",
            "What’s your favorite season?",
            "How do you like your coffee?",
            "What’s your guilty pleasure?",
            "What’s your favorite music genre?",
            "Tell me your secret!",
            "What would you do with a million dollars?",
            "Do you believe in aliens?",
            "What’s your hidden talent?",
            "What would you change about the world?",
            "What’s your favorite childhood memory?",
            "What’s something you've always wanted to try?",
            "What’s your go-to snack?",
            "If you could travel anywhere, where would you go?",
            "What's your favorite way to relax?",
            "Do you prefer summer or winter?",
            "What inspires you?",
            "How do you stay motivated?",
            "What's your favorite animal?",
            "If you could have dinner with anyone, who would it be?"
        )

        return names.mapIndexed { index, name ->
            // Randomly assign silent status (true or false)
            Message(
                name = name,
                content = messages.getOrElse(index % messages.size) { "" },
                isSilent = false
            ) // You can set this to true or false as needed
        }
    }
}
