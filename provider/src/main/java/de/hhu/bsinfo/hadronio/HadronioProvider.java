package de.hhu.bsinfo.hadronio;

import de.hhu.bsinfo.hadronio.generated.BuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.stream.Collectors;

public class HadronioProvider extends SelectorProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HadronioProvider.class);

    private final Configuration configuration;
    private final UcxProvider provider;

    public HadronioProvider() {
        if (System.getProperty("java.nio.channels.spi.SelectorProvider").equals("de.hhu.bsinfo.hadronio.HadronioProvider")) {
            LOGGER.info("de.hhu.bsinfo.hadronio.HadronioProvider is set as default SelectorProvider -> hadroNIO is active");
        } else {
            throw new IllegalStateException("de.hhu.bsinfo.hadronio.HadronioProvider is not set as default SelectorProvider -> hadroNIO is not active");
        }

        configuration = Configuration.getInstance();
        LOGGER.info("hadroNIO configuration: [{}]", configuration);

        try {
            provider = (UcxProvider) Class.forName(configuration.getProviderClass()).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to instantiate class '" + configuration.getProviderClass() + "'!", e);
        }
    }

    @Override
    public DatagramChannel openDatagramChannel() throws IOException {
        throw new UnsupportedOperationException("Operation not supported!");
    }

    @Override
    public DatagramChannel openDatagramChannel(ProtocolFamily protocolFamily) throws IOException {
        throw new UnsupportedOperationException("Operation not supported!");
    }

    @Override
    public Pipe openPipe() throws IOException {
        throw new UnsupportedOperationException("Operation not supported!");
    }

    @Override
    public AbstractSelector openSelector() throws IOException {
        LOGGER.info("Creating new UcxSelector");

        return new HadronioSelector(this);
    }

    @Override
    public ServerSocketChannel openServerSocketChannel() throws IOException {
        LOGGER.info("Creating new UcxServerSocketChannel");

        final UcxServerSocketChannel serverSocketChannel = provider.createServerSocketChannel();
        return new HadronioServerSocketChannel(this, serverSocketChannel, configuration);
    }

    @Override
    public SocketChannel openSocketChannel() throws IOException {
        LOGGER.info("Creating new UcxSocketChannel");

        final UcxSocketChannel socketChannel = provider.createSocketChannel();
        return new HadronioSocketChannel(this, socketChannel, configuration);
    }

    public static void printBanner() {
        final InputStream inputStream = HadronioProvider.class.getClassLoader().getResourceAsStream("banner.txt");

        if (inputStream == null) {
            return;
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final String banner = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        System.out.print("\n");
        System.out.printf(banner, BuildConfig.VERSION, BuildConfig.BUILD_DATE, BuildConfig.GIT_BRANCH, BuildConfig.GIT_COMMIT);
        System.out.print("\n\n");
    }
}
