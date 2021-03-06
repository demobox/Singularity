package com.hubspot.singularity.runner.base.sentry;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.singularity.runner.base.configuration.SingularityRunnerBaseConfiguration;

import net.kencochrane.raven.Raven;
import net.kencochrane.raven.RavenFactory;
import net.kencochrane.raven.event.Event;
import net.kencochrane.raven.event.EventBuilder;
import net.kencochrane.raven.event.interfaces.ExceptionInterface;

@Singleton
public class SingularityRunnerExceptionNotifier {
  private final Optional<Raven> raven;
  private final SingularityRunnerBaseConfiguration configuration;

  @Inject
  public SingularityRunnerExceptionNotifier(SingularityRunnerBaseConfiguration configuration) {
    this.configuration = configuration;
    if (configuration.getSentryDsn().isPresent()) {
      this.raven = Optional.of(RavenFactory.ravenInstance(configuration.getSentryDsn().get()));
    } else {
      this.raven = Optional.absent();
    }
  }

  private String getPrefix() {
    if (Strings.isNullOrEmpty(configuration.getSentryPrefix())) {
      return "";
    }

    return configuration.getSentryPrefix() + " ";
  }

  private String getCallingClassName(StackTraceElement[] stackTrace) {
    if (stackTrace != null && stackTrace.length > 2) {
      return stackTrace[2].getClassName();
    } else {
      return "(unknown)";
    }
  }

  private void sendEvent(Raven raven, final EventBuilder eventBuilder) {
    raven.runBuilderHelpers(eventBuilder);

    raven.sendEvent(eventBuilder.build());
  }

  public void notify(Throwable t, Map<String, String> extraData) {
    if (!raven.isPresent()) {
      return;
    }

    final StackTraceElement[] currentThreadStackTrace = Thread.currentThread().getStackTrace();

    final EventBuilder eventBuilder = new EventBuilder()
            .setCulprit(getPrefix() + t.getMessage())
            .setMessage(Strings.nullToEmpty(t.getMessage()))
            .setLevel(Event.Level.ERROR)
            .setLogger(getCallingClassName(currentThreadStackTrace))
            .addSentryInterface(new ExceptionInterface(t));

    if (extraData != null && !extraData.isEmpty()) {
      for (Map.Entry<String, String> entry : extraData.entrySet()) {
        eventBuilder.addExtra(entry.getKey(), entry.getValue());
      }
    }

    sendEvent(raven.get(), eventBuilder);
  }

  public void notify(String subject, Map<String, String> extraData) {
    if (!raven.isPresent()) {
      return;
    }

    final StackTraceElement[] currentThreadStackTrace = Thread.currentThread().getStackTrace();

    final EventBuilder eventBuilder = new EventBuilder()
            .setMessage(getPrefix() + subject)
            .setLevel(Event.Level.ERROR)
            .setLogger(getCallingClassName(currentThreadStackTrace));

    if (extraData != null && !extraData.isEmpty()) {
      for (Map.Entry<String, String> entry : extraData.entrySet()) {
        eventBuilder.addExtra(entry.getKey(), entry.getValue());
      }
    }

    sendEvent(raven.get(), eventBuilder);
  }
}
