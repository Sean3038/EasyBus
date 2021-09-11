package com.examprepare.easybus.feature.routedetail.usecase

import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.repository.RouteRepository
import com.examprepare.easybus.feature.repository.exception.NoRouteFailure
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetRouteTest : TestCase() {

    companion object {
        private const val TEST_ROUTE_ID = "Test "
    }

    @Test
    fun `test should return right either contain route when get route from repository contain route`() {
        //Given
        val routeRepository = createRouteRepositoryContainRoute()

        val getRoute = GetRoute(routeRepository)
        val param = GetRoute.Params(TEST_ROUTE_ID)

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = getRoute.run(param)
        }

        //Assert
        assertTrue(result.isRight)
        result.fold({
            fail("result failure")
        }, {
            assertTrue(it != Route.empty)
        })
    }

    @Test
    fun `test should return left either with no route failure when get route from repository not contain route`() {
        //Given
        val routeRepository = createRouteRepositoryNotContainRoute()

        val getRoute = GetRoute(routeRepository)
        val param = GetRoute.Params(TEST_ROUTE_ID)

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = getRoute.run(param)
        }

        //Assert
        assertTrue(result.isLeft)
        result.fold({
            assertEquals(NoRouteFailure, it)
        }, {
            fail("should not have route result")
        })
    }

    private fun createRouteRepositoryNotContainRoute(): RouteRepository {
        val routeRepository = mock<RouteRepository>()
        runBlocking {
            whenever(routeRepository.route(any())).thenReturn(Either.Left(NoRouteFailure))
        }
        return routeRepository
    }

    private fun createRouteRepositoryContainRoute(): RouteRepository {
        val routeRepository = mock<RouteRepository>()
        val route = createRoute()
        runBlocking {
            whenever(routeRepository.route(TEST_ROUTE_ID)).thenReturn(Either.Right(route))
        }
        return routeRepository
    }

    private fun createRoute(): Route = mock()
}