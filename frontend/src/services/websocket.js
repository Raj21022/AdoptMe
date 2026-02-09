import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

let stompClient = null;
let activeSocket = null;
let activeSubscription = null;

export const connect = (conversationKey, onMessageReceived) => {
  if (!conversationKey) return;

  disconnect();

  activeSocket = new SockJS('http://localhost:8080/ws');
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
