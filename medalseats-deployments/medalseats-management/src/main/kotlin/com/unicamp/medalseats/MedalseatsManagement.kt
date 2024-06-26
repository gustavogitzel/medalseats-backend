package com.unicamp.medalseats

import com.medalseats.adapter.cyrptograph.HashCryptographyService
import com.medalseats.adapter.http.command.account.AccountHttpHandler
import com.medalseats.adapter.http.command.payment.PaymentCommandHttpHandler
import com.medalseats.adapter.http.command.routerManagement
import com.medalseats.adapter.http.common.CorsConfiguration
import com.medalseats.adapter.http.query.match.MatchHttpHandler
import com.medalseats.adapter.http.query.payment.PaymentQueryHttpHandler
import com.medalseats.adapter.http.query.router
import com.medalseats.adapter.r2dbc.R2dbcTransactionScope
import com.medalseats.adapter.r2dbc.account.AccountR2dbcRepository
import com.medalseats.adapter.r2dbc.match.MatchR2dbcRepository
import com.medalseats.adapter.r2dbc.payment.PaymentR2dbcRepository
import com.medalseats.application.command.account.CreateAccountCommandHandler
import com.medalseats.application.command.account.SignInAccountCommandHandler
import com.medalseats.application.command.payment.AuthorizePaymentCommandHandler
import com.medalseats.application.command.payment.CapturePaymentCommandHandler
import com.medalseats.application.command.payment.ExpirePaymentCommandHandler
import com.medalseats.application.command.payment.RefundPaymentCommandHandler
import com.medalseats.application.query.match.FindAllMatchesQueryHandler
import com.medalseats.application.query.match.FindMatchByIdQueryHandler
import com.medalseats.application.query.payment.FindPaymentsByEmailQueryHandler
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.Locale
import kotlin.reflect.KClass

@SpringBootApplication
@ConfigurationPropertiesScan
class MedalseatsManagement

fun rootRouter() = coRouter {
    GET("") { ok().buildAndAwait() }
}

fun beans(context: GenericApplicationContext) = beans {
    // HTTP handlers
    bean(::rootRouter)
    bean(::router)
    bean(::routerManagement)

    // Repositories
    bean<MatchR2dbcRepository>()
    bean<AccountR2dbcRepository>()
    bean<PaymentR2dbcRepository>()

    // Cryptography
    bean {
        HashCryptographyService(
            ref<MedalseatsManagementProperties>().passwordEncoder
        )
    }

    // HTTP handlers
    bean<MatchHttpHandler>()
    bean<AccountHttpHandler>()
    bean<PaymentCommandHttpHandler>()
    bean<PaymentQueryHttpHandler>()

    // Query handlers
    bean<FindMatchByIdQueryHandler>()
    bean<FindAllMatchesQueryHandler>()
    bean<FindPaymentsByEmailQueryHandler>()

    // Command handlers
    bean<CreateAccountCommandHandler>()
    bean<SignInAccountCommandHandler>()
    bean<AuthorizePaymentCommandHandler>()
    bean<CapturePaymentCommandHandler>()
    bean<ExpirePaymentCommandHandler>()
    bean<RefundPaymentCommandHandler>()

    // Transaction scope
    bean<R2dbcTransactionScope>()

    // Cors filter
    bean<CorsConfiguration>()
}

fun <T : Any> BeanDefinitionDsl.bean(context: GenericApplicationContext, type: KClass<T>) {
    context.registerBean(
        BeanDefinitionReaderUtils.uniqueBeanName(type.java.name, context),
        type.java,
        BeanDefinitionCustomizer {}
    )
}

class MedalseatsManagementInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beans(context).initialize(context)
}

fun main(args: Array<String>) {
    Locale.setDefault(Locale("pt", "BR"))
    runApplication<MedalseatsManagement>(*args)
}
