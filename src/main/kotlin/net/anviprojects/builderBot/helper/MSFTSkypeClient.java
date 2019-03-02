package net.anviprojects.builderBot.helper;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.samczsun.skype4j.exceptions.handler.ErrorHandler;
import com.samczsun.skype4j.exceptions.handler.ErrorSource;
import com.samczsun.skype4j.internal.client.FullClient;
import com.samczsun.skype4j.internal.threads.AuthenticationChecker;
import com.samczsun.skype4j.internal.threads.KeepaliveThread;
import com.samczsun.skype4j.internal.utils.UncheckedRunnable;
import com.samczsun.skype4j.user.ContactRequest;

import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Copiright: https://gist.github.com/Manevolent/1c6bc379c10c1e50358e8f2e2356fef7
 */
public class MSFTSkypeClient extends FullClient {

	private MSFTSkypeClient(String skypeToken, String skypeId,
							Set<String> resources, Logger customLogger,
							List<ErrorHandler> errorHandlers) {
		super(skypeId, null, resources, customLogger, errorHandlers);

		setSkypeToken(skypeToken);
	}

	@Override
	public void login() {
		try {
			HttpURLConnection asmResponse = null;
			try {
				asmResponse = this.getAsmToken();
			} catch (ConnectionException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}


			List<UncheckedRunnable> tasks = new ArrayList<>();
			tasks.add(this::registerEndpoint);
            tasks.add(this::loadAllContacts);
			tasks.add(() -> this.getContactRequests(false));
			tasks.add(() -> {
				try {
					this.registerWebSocket();
				} catch (Exception e) {
					handleError(ErrorSource.REGISTERING_WEBSOCKET, e, false);
				}
			});

			try {
				ExecutorService executorService = Executors.newFixedThreadPool(5);
				tasks.forEach(executorService::submit);
				executorService.shutdown();
				executorService.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			loggedIn.set(true);
			(sessionKeepaliveThread = new KeepaliveThread(this)).start();
			(reauthThread = new AuthenticationChecker(this)).start();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	// I override this for high-level login stuff
	@Override
	public void reauthenticate() throws ConnectionException, InvalidCredentialsException, NotParticipatingException {
		doShutdown();

		login();

		if(subscribed.get())
			subscribe();
	}

	public Set<ContactRequest> getAllContactRequests() {
		return this.allContactRequests;
	}

	public static class Builder {
		private final String skypeToken;
		private final String skypeId;

		private Set<String> resources = new HashSet();
		private List<ErrorHandler> errorHandlers = new ArrayList();
		private Logger customLogger;

		public Builder(String skypeToken, String skypeId) {
			this.skypeToken = skypeToken;
			this.skypeId = skypeId;
		}

		public Builder withAllResources() {
			this.resources.addAll(Arrays.asList("/v1/users/ME/conversations/ALL/properties", "/v1/users/ME/conversations/ALL/messages", "/v1/users/ME/contacts/ALL", "/v1/threads/ALL"));
			return this;
		}

		public Builder withResource(String resource) {
			this.resources.add(resource);
			return this;
		}

		public Builder withLogger(Logger logger) {
			this.customLogger = logger;
			return this;
		}

		public Builder withExceptionHandler(ErrorHandler errorHandler) {
			this.errorHandlers.add(errorHandler);
			return this;
		}

		public Skype build() {
			if(this.resources.isEmpty()) {
				throw new IllegalArgumentException("No resources selected");
			} else if(this.skypeToken != null) {
				return new MSFTSkypeClient(this.skypeToken, this.skypeId, this.resources, this.customLogger, this.errorHandlers);
			} else {
				throw new IllegalArgumentException("No skype token specified");
			}
		}
	}
}
