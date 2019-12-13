package com.karumi

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.matchers.RecyclerViewItemsCountMatcher.Companion.recyclerViewHasItemCount
import com.karumi.ui.view.MainActivity
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    @Mock
    lateinit var repository: SuperHeroRepository

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        startActivity()

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()))
    }

    @Test
    fun showsHeroesCaseIfThereAreSuperHeroes() {
        givenSuperHeroes(1)

        startActivity()

        onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())))
        onView(withText("SpoilerMan")).check(matches(isDisplayed()))
    }

    @Test
    fun shows20HeroesCaseIfThereAre20SuperHeroes() {
        givenSuperHeroes(20)

        startActivity()

        onView(withId(R.id.recycler_view)).check(
            matches(recyclerViewHasItemCount(20))
        )
    }

    @Test
    fun checkTheSuperHeroNameIsShown() {
        givenSuperHeroes(20)
        givenSuperHeroByName("SpoilerMan1")

        startActivity()

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText("SpoilerMan1")).check(matches(isDisplayed()))
        onView(withText("Description")).check(matches(isDisplayed()))
        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSuperHeroNotFoundErrorShown() {
        givenSuperHeroes(20)
        givenSuperHeroByName("Random")

        startActivity()

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText("There is not SuperHero with that name!!!")).check(matches(isDisplayed()))
    }


    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(emptyList())
    }

    private fun givenSuperHeroByName(name: String) {
        whenever(repository.getByName(name)).thenReturn(givenSuperHero())
    }

    private fun givenSuperHeroes(count: Int) {
        whenever(repository.getAllSuperHeroes()).thenReturn(
            getSuperHeroList(count)
        )
    }

    private fun getSuperHeroList(count: Int): MutableList<SuperHero> {
        val heroesList = mutableListOf<SuperHero>()
        for (x in 1..count) {
            heroesList.add(givenSuperHero(item = x))
        }
        return heroesList
    }

    private fun givenSuperHero(name: String = "SpoilerMan", item: Int = 0) = SuperHero(
        name + item,
        "https://statics.memondo.com/p/99/crs/2016/06/CR_1011264_091d1169f3954e11bfd16850e62a498e_spoilerman.jpg?cb=1282006",
        true,
        "Description"
    )

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}