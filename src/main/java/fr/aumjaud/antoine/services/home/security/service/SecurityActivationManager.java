package fr.aumjaud.antoine.services.home.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityActivationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private boolean isActivated;

    @Autowired
    private MessageService messageService;

    private enum Action {
        NOTHING, ACTIVATE, ACTIVATE_NOW, DESACTIVATE_NOW, EVENT
    }

    private Action action = Action.NOTHING;

    private final Object actionLock = new Object();
    private final Object pendingLock = new Object();

    private int activateActionStep, eventActionStep;

    private void setAction(Action action) {
        LOGGER.info("Send action {}", action);

        this.action = action;
        synchronized (actionLock) {
            LOGGER.trace("main - launch signal ACTION ");
            actionLock.notify();
        }
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void activateNow() {
        setAction(Action.ACTIVATE_NOW);
    }

    public void activate() {
        if (activateActionStep == 0)
            setAction(Action.ACTIVATE);
    }

    public void desactivateNow() {
        setAction(Action.DESACTIVATE_NOW);
    }

    public void event() {
        if (eventActionStep == 0)
            setAction(Action.EVENT);
    }

    private PendingActionRunnable pendingActionRunnable = new PendingActionRunnable();

    public SecurityActivationManager() {
        Thread actionThread = new Thread(new ActionRunnable());
        actionThread.start();

        Thread timeoutThread = new Thread(pendingActionRunnable);
        timeoutThread.start();
    }

    private boolean threadIsRunning = true;

    private class ActionRunnable implements Runnable {

        @Override
        public void run() {
            while (threadIsRunning) {
                synchronized(actionLock) {
                    try {
                        LOGGER.trace("--action: wait a signal");
                        actionLock.wait();
                        LOGGER.trace("--action: signal !!");
                    } catch (InterruptedException e) {
                        threadIsRunning = false;
                    }
                }

                switch (action) {
                case ACTIVATE_NOW:
                    isActivated = true;
                    activateActionStep = 0;
                    eventActionStep = 0;
                    messageService.activation();
                    break;
                case DESACTIVATE_NOW:
                    isActivated = false;
                    activateActionStep = 0;
                    eventActionStep = 0;
                    messageService.desactivation();
                    break;
                case ACTIVATE:
                    switch (activateActionStep++) {
                    case 0:
                        messageService.notif("Tu as 1 minutes pour fermer la maison");
                        launchAction(30);
                        break;
                    case 1:
                        messageService.notif("30 secondes");
                        launchAction(15);
                        break;
                    case 2:
                        messageService.notif("15 secondes");
                        launchAction(10);
                        break;
                    case 3:
                        messageService.notif("5 secondes");
                        launchAction(5);
                        break;
                    case 4:
                        messageService.activation();
                        isActivated = true;
                        activateActionStep = 0;
                        break;
                    default:
                        LOGGER.debug(" default activateActionStep");
                    }
                    break;
                case EVENT:
                    if (eventActionStep++ < 5) {
                        messageService.alerte(eventActionStep);
                        launchAction(10);
                    } else {
                        eventActionStep = 0;
                        messageService.intrusion();
                    }
                    break;
                default:
                    break;
                }
            }
        }

        private void launchAction(int timeInMs) {
            pendingActionRunnable.setDelay(timeInMs);
            synchronized (pendingLock) {
                LOGGER.trace("action : signal PENDING launch");
                pendingLock.notify();
            }
        }
    }

    private class PendingActionRunnable implements Runnable {
        private int timeInMs;

        public void setDelay(int timeInMs) {
            this.timeInMs = timeInMs;
        }

        @Override
        public void run() {
            while(threadIsRunning) {
                synchronized(pendingLock) { 
                    try {
                        LOGGER.trace("--pending: wait a signal");
                        pendingLock.wait();
                        LOGGER.trace("--pending: signal !!");
                        Thread.sleep(timeInMs * 1000);
                    } catch (InterruptedException e) {
                        threadIsRunning = false;
                    }
                }
                synchronized(actionLock) {
                    actionLock.notify();
                    LOGGER.trace("--pending:  signal ACTION launch");
                } 
            }
        }
    }
}