package com.examprepare.easybus.core.model.network

data class RouteNetworkEntity(
    val AuthorityID: String,
    val BusRouteType: Int,
    val City: String,
    val CityCode: String,
    val DepartureStopNameEn: String,
    val DepartureStopNameZh: String,
    val DestinationStopNameEn: String,
    val DestinationStopNameZh: String,
    val FareBufferZoneDescriptionEn: String,
    val FareBufferZoneDescriptionZh: String,
    val HasSubRoutes: Boolean,
    val Operators: List<Operator>,
    val ProviderID: String,
    val RouteID: String,
    val RouteMapImageUrl: String,
    val RouteName: RouteName,
    val RouteUID: String,
    val SubRoutes: List<SubRoute>,
    val TicketPriceDescriptionEn: String,
    val TicketPriceDescriptionZh: String,
    val UpdateTime: String,
    val VersionID: Int
)

data class Operator(
    val OperatorCode: String,
    val OperatorID: String,
    val OperatorName: OperatorName,
    val OperatorNo: String
)

data class RouteName(
    val En: String,
    val Zh_tw: String
)

data class SubRoute(
    val Direction: Int,
    val FirstBusTime: String,
    val HolidayFirstBusTime: String,
    val HolidayLastBusTime: String,
    val LastBusTime: String,
    val OperatorIDs: List<String>,
    val SubRouteID: String,
    val SubRouteName: SubRouteName,
    val SubRouteUID: String
)

data class OperatorName(
    val En: String,
    val Zh_tw: String
)

data class SubRouteName(
    val En: String,
    val Zh_tw: String
)