package ru.kpfu.itis.wwhelper.util

object Clothes {

    val chestClothes = arrayOf("Jumper", "T-Shirt", "Sweater", "Shirt", "Jacket", "Cardigan", "Dress", "Sweatshirt")

    val legsClothes = arrayOf("Trousers", "Jeans", "Shorts", "Skirt")

    val outerClothes = arrayOf("Anorak", "Coat", "Parka", "Down-padded coat", "Fur coat", "Bomber jacket")

    val forHotEasy = listOf("T-Shirt", "Shirt", "Shorts", "Trousers", "Skirt", "Dress")

    val forHotMedium = listOf("Jeans") + forHotEasy

    val forWarmEasy = listOf("Jeans", "Shirt", "T-Shirt", "Jacket", "Trousers", "Dress")

    val forWarmMedium = forWarmEasy + "Cardigan" + "Jumper" + "Sweatshirt"

    val forNormalEasy = listOf("Jumper", "T-Shirt", "Sweater", "Shirt", "Jacket", "Cardigan", "Dress", "Sweatshirt",
            "Trousers", "Jeans", "Skirt", "Bomber jacket")

    val forNormalMedium = forNormalEasy

    val forNormalHard = forNormalMedium + "Coat"

    val forColdEasy = listOf("Jumper", "T-Shirt", "Sweater", "Shirt", "Jacket", "Cardigan", "Sweatshirt",
            "Trousers", "Jeans", "Skirt", "Anorak", "Down-padded coat", "Bomber jacket", "Coat")

    val forFreezingEasy = listOf("Jumper", "T-Shirt", "Sweater", "Shirt", "Jacket", "Cardigan", "Sweatshirt",
            "Trousers", "Jeans", "Skirt", "Anorak", "Down-padded coat", "Coat")

    val forWinter = listOf("Jumper", "T-Shirt", "Sweater", "Shirt", "Jacket", "Cardigan", "Sweatshirt",
            "Trousers", "Jeans", "Anorak", "Parka", "Fur coat")
}