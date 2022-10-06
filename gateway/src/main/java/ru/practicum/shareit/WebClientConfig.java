package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfig {
    @Value("${shareit-server.url}")
    private String serviceUrl;


    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        RestTemplateBuilder restTemblate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl));
        return new UserClient(serviceUrl, restTemblate);
    }

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        RestTemplateBuilder restTemblate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl));
        return new BookingClient(serviceUrl, restTemblate);
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        RestTemplateBuilder restTemblate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl));
        return new ItemClient(serviceUrl, restTemblate);
    }

    @Bean
    public ItemRequestClient requestClient(RestTemplateBuilder builder) {
        RestTemplateBuilder restTemblate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl));
        return new ItemRequestClient(serviceUrl, restTemblate);
    }

}
