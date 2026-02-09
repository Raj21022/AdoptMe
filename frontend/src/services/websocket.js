import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

let stompClient = null;
let activeSocket = null;
let activeSubscription = null;

const resolveWsUrl = () => {
  const explicit = import.meta.env.VITE_WS_URL;
  if (explicit) return explicit;

  const apiBase = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  const base = apiBase.replace(/\/api\/?$/i, '');
  const wsUrl = `${base}/ws`;

  // If the app is served over HTTPS, ensure we don't initiate an insecure SockJS connection.
  if (typeof window !== 'undefined' && window.location?.protocol === 'https:') {
    return wsUrl.replace(/^http:/i, 'https:');
  }

  return wsUrl;
};

export const connect = (conversationKey, onMessageReceived) => {
  if (!conversationKey) return;

  disconnect();

  activeSocket = new SockJS(resolveWsUrl());
  stompClient = Stomp.over(activeSocket);

  // Disable verbose logs in browser console.
  stompClient.debug = () => {};

  stompClient.connect({}, () => {
    if (!stompClient || !stompClient.connected) {
      return;
    }

    activeSubscription = stompClient.subscribe(`/topic/chat/${conversationKey}`, (message) => {
      const receivedMessage = JSON.parse(message.body);
      onMessageReceived(receivedMessage);
    });
  }, (error) => {
    console.error('WebSocket connection error:', error);
  });
};

export const sendMessage = (senderId, receiverId, content) => {
  if (stompClient && stompClient.connected) {
    stompClient.send('/app/send', {}, JSON.stringify({
      senderId,
      receiverId,
      content
    }));
  }
};

export const disconnect = () => {
  try {
    if (activeSubscription) {
      activeSubscription.unsubscribe();
      activeSubscription = null;
    }

    if (stompClient) {
      // Avoid sending DISCONNECT before SockJS is fully open.
      if (stompClient.connected) {
        stompClient.disconnect(() => {});
      }
      stompClient = null;
    }

    if (activeSocket && typeof activeSocket.close === 'function') {
      activeSocket.close();
    }
    activeSocket = null;
  } catch (error) {
    console.error('WebSocket disconnect error:', error);
    stompClient = null;
    activeSocket = null;
    activeSubscription = null;
  }
};
