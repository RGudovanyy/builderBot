package net.anviprojects.builderBot.model

data class BuildPlan (val name : String, var teamcity : Teamcity?, val aliases : List<String>)