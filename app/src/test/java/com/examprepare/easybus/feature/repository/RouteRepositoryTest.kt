package com.examprepare.easybus.feature.repository

import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.database.dao.RouteEntityDao
import com.examprepare.easybus.core.exception.Failure
import com.examprepare.easybus.core.functional.Either
import com.examprepare.easybus.core.model.local.RouteLocalEntity
import com.examprepare.easybus.core.model.network.RouteName
import com.examprepare.easybus.core.model.network.RouteNetworkEntity
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.model.Route
import com.examprepare.easybus.feature.repository.exception.NoRouteFailure
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class RouteRepositoryTest {

    companion object {
        private const val TEST_ROUTE_ID = "12345"
        private const val TEST_ROUTE_NAME = "testRouteName"
        private const val TEST_ROUTE_DEPARTURE_STOP_NAME = "testRouteDepartureStopName"
        private const val TEST_ROUTE_DESTINATION_STOP_NAME = "testRouteDestinationStopName"
        private val TEST_RESOURCE_CITY_ARRAY = arrayOf("CITY01,CITY02")
    }

    @Test
    fun `should return route when local ever cached route data`() {
        //Given
        val mockPTXService = createPTXService()
        val mockNetworkHandler = createNetworkHandler()
        val mockPTXDataBase = createMockPTXDataBase()

        val routeRouteLocalEntity = RouteRepository.Impl(
            TEST_RESOURCE_CITY_ARRAY,
            mockNetworkHandler,
            mockPTXService,
            mockPTXDataBase
        )

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = routeRouteLocalEntity.route(TEST_ROUTE_ID)
        }

        //Assert
        assertTrue(result.isRight)
        result.fold({
            fail("result failure")
        }, {
            assertEquals(TEST_ROUTE_ID, it.routeId)
            assertEquals(TEST_ROUTE_NAME, it.routeName)
            assertEquals(TEST_ROUTE_DEPARTURE_STOP_NAME, it.departureStopName)
            assertEquals(TEST_ROUTE_DESTINATION_STOP_NAME, it.destinationStopName)
        })
    }

    @Test
    fun `should return route when retrieve route entity from remote`() {
        //Given
        val mockPTXService = createPTXService()
        val mockNetworkHandler = createNetworkHandler()
        val mockPTXDataBase = createMockPTXDataBaseNotContainRouteEntity()

        val routeRouteLocalEntity = RouteRepository.Impl(
            TEST_RESOURCE_CITY_ARRAY,
            mockNetworkHandler,
            mockPTXService,
            mockPTXDataBase
        )

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = routeRouteLocalEntity.route(TEST_ROUTE_ID)
        }

        //Assert
        assertTrue(result.isRight)
        result.fold({
            fail("result failure")
        }, {
            assertEquals(TEST_ROUTE_ID, it.routeId)
            assertEquals(TEST_ROUTE_NAME, it.routeName)
            assertEquals(TEST_ROUTE_DEPARTURE_STOP_NAME, it.departureStopName)
            assertEquals(TEST_ROUTE_DESTINATION_STOP_NAME, it.destinationStopName)
        })
    }

    @Test
    fun `should return no route failure when repository not able to retrieve route`() {
        //Given
        val mockPTXService = createPTXServiceNotContainRouteNetworkEntity()
        val mockNetworkHandler = createNetworkHandler()
        val mockPTXDataBase = createMockPTXDataBaseNotContainRouteEntity()

        val routeRouteLocalEntity = RouteRepository.Impl(
            TEST_RESOURCE_CITY_ARRAY,
            mockNetworkHandler,
            mockPTXService,
            mockPTXDataBase
        )

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = routeRouteLocalEntity.route(TEST_ROUTE_ID)
        }

        //Assert
        assertTrue(result.isLeft)
        result.fold({
            assertEquals(NoRouteFailure, it)
        }, {
            fail("should not have route result")
        })
    }

    @Test
    fun `should return network connection failure when network is disable`() {
        //Given
        val mockPTXService = mock<PTXService>()
        val mockNetworkHandler = createNetworkHandlerWithNetworkDisable()
        val mockPTXDataBase = createMockPTXDataBase()

        val routeRouteLocalEntity = RouteRepository.Impl(
            TEST_RESOURCE_CITY_ARRAY,
            mockNetworkHandler,
            mockPTXService,
            mockPTXDataBase
        )

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = routeRouteLocalEntity.route(TEST_ROUTE_ID)
        }

        //Assert
        assertTrue(result.isLeft)
        result.fold({
            assertEquals(Failure.NetworkConnection, it)
        }, {
            fail("should not have route result")
        })
    }

    @Test
    fun `should return server error failure when get route data and throw exception`() {
        //Given
        val mockPTXService = createPTXServiceThrowExceptionWhenRetrieveData()
        val mockNetworkHandler = createNetworkHandler()
        val mockPTXDataBase = createMockPTXDataBaseNotContainRouteEntity()

        val routeRouteLocalEntity = RouteRepository.Impl(
            TEST_RESOURCE_CITY_ARRAY,
            mockNetworkHandler,
            mockPTXService,
            mockPTXDataBase
        )

        //When
        var result: Either<Failure, Route>
        runBlocking {
            result = routeRouteLocalEntity.route(TEST_ROUTE_ID)
        }

        //Assert
        assertTrue(result.isLeft)
        result.fold({
            assertEquals(Failure.ServerError, it)
        }, {
            fail("should not have route result")
        })
    }

    private fun createMockPTXDataBase(): PTXDataBase {
        val mockPTXDataBase = mock<PTXDataBase>()
        val mockRouteEntityDao = mock<RouteEntityDao>()
        val mockRouteResult = createTestRouteLocalEntities()

        whenever(mockPTXDataBase.routeEntity()).thenReturn(mockRouteEntityDao)

        runBlocking {
            whenever(mockRouteEntityDao.get(TEST_ROUTE_ID)).thenReturn(mockRouteResult)
        }

        return mockPTXDataBase
    }

    private fun createMockPTXDataBaseNotContainRouteEntity(): PTXDataBase {
        val mockPTXDataBase = mock<PTXDataBase>()
        val mockRouteEntityDao = mock<RouteEntityDao>()

        whenever(mockPTXDataBase.routeEntity()).thenReturn(mockRouteEntityDao)
        runBlocking {
            whenever(mockRouteEntityDao.get(TEST_ROUTE_ID)).thenReturn(listOf())
        }

        return mockPTXDataBase
    }

    private fun createNetworkHandler(): NetworkHandler {
        val mockNetworkHandler = mock<NetworkHandler>()
        whenever(mockNetworkHandler.isNetworkAvailable()).thenReturn(true)
        return mockNetworkHandler
    }

    private fun createNetworkHandlerWithNetworkDisable(): NetworkHandler {
        val mockNetworkHandler = mock<NetworkHandler>()
        whenever(mockNetworkHandler.isNetworkAvailable()).thenReturn(false)
        return mockNetworkHandler
    }

    private fun createPTXService(): PTXService {
        val mockPTXService = mock<PTXService>()
        val entity = mock<RouteNetworkEntity>()
        val routeName = mock<RouteName>()
        whenever(entity.RouteID).thenReturn(TEST_ROUTE_ID)
        whenever(entity.RouteName).thenReturn(routeName)
        whenever(routeName.Zh_tw).thenReturn(TEST_ROUTE_NAME)
        whenever(entity.DepartureStopNameZh).thenReturn(TEST_ROUTE_DEPARTURE_STOP_NAME)
        whenever(entity.DestinationStopNameZh).thenReturn(TEST_ROUTE_DESTINATION_STOP_NAME)


        runBlocking {
            whenever(mockPTXService.getRoute(any(), any())).thenReturn(entity)
        }

        return mockPTXService
    }

    private fun createPTXServiceNotContainRouteNetworkEntity(): PTXService {
        val mockPTXService = mock<PTXService>()

        runBlocking {
            whenever(mockPTXService.getRoute(any(), any())).thenReturn(null)
        }

        return mockPTXService
    }

    private fun createPTXServiceThrowExceptionWhenRetrieveData(): PTXService {
        val mockPTXService = mock<PTXService>()

        runBlocking {
            whenever(
                mockPTXService.getRoute(
                    any(),
                    any()
                )
            ).thenThrow(RuntimeException("Test Exception"))
        }

        return mockPTXService
    }


    private fun createTestRouteLocalEntities(): List<RouteLocalEntity> {
        val testEntity = RouteLocalEntity(
            TEST_ROUTE_ID,
            TEST_ROUTE_NAME,
            TEST_ROUTE_DEPARTURE_STOP_NAME,
            TEST_ROUTE_DESTINATION_STOP_NAME
        )
        return listOf(testEntity)
    }

}
