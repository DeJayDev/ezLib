package dev.dejay.ezlib.components.commands;

import dev.dejay.ezlib.components.i18n.I;
import dev.dejay.ezlib.components.worker.Worker;
import dev.dejay.ezlib.components.worker.WorkerAttributes;
import dev.dejay.ezlib.components.worker.WorkerCallback;
import dev.dejay.ezlib.components.worker.WorkerRunnable;
import dev.dejay.ezlib.tools.PluginLogger;
import dev.dejay.ezlib.tools.mojang.UUIDFetcher;
import java.util.UUID;
import java.util.function.Consumer;

@WorkerAttributes(name = "Command's worker", queriesMainThread = true)
public class CommandWorkers extends Worker {

    /**
     * Fetches an offline player's UUID by name.
     */
    public void offlineNameFetch(final String playerName, final Consumer<UUID> callback) {
        final WorkerCallback wCallback = new WorkerCallback<UUID>() {
            @Override
            public void finished(UUID result) {
                callback.accept(result);  // Si tout va bien on passe l'UUID au callback
            }

            @Override
            public void errored(Throwable exception) {
                PluginLogger.warning(I.t("Error while getting player UUID"));
                callback.accept(null);  // En cas d'erreur on envoie `null` au callback
            }
        };
        WorkerRunnable wr = new WorkerRunnable<UUID>() {
            @Override
            public UUID run() throws Throwable {
                return UUIDFetcher.fetch(playerName);
            }
        };
        submitQuery(wr, wCallback);
    }

}
