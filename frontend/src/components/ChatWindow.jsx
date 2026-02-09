import React, { useState, useEffect, useRef } from 'react';
import api from '../services/api';
import { connect, sendMessage, disconnect } from '../services/websocket';

function ChatWindow({ currentUserId, otherUserId, otherUserName }) {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const messagesEndRef = useRef(null);
  const conversationKey = [currentUserId, otherUserId].sort((a, b) => a - b).join('_');

  useEffect(() => {
    loadConversation();
    
    connect(conversationKey, (message) => {
      if (message.senderId === otherUserId || message.receiverId === otherUserId) {
        setMessages(prev => [...prev, message]);
      }
    });

    return () => {
      disconnect();
    };
  }, [currentUserId, otherUserId, conversationKey]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const loadConversation = async () => {
    try {
      const response = await api.get(`/chat/conversation?user1=${currentUserId}&user2=${otherUserId}`);
      setMessages(response.data);
    } catch (error) {
      console.error('Error loading conversation:', error);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleSend = () => {
    if (newMessage.trim()) {
      sendMessage(currentUserId, otherUserId, newMessage);
      setNewMessage('');
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="flex flex-col h-[75vh] md:h-[600px] bg-white">
      {/* 1. Chat Header */}
      <div className="px-6 py-4 border-b border-slate-100 bg-slate-50/50 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-brand-100 rounded-full flex items-center justify-center text-brand-600 font-bold">
            {otherUserName.charAt(0)}
          </div>
          <div>
            <h3 className="font-heading font-bold text-slate-800">{otherUserName}</h3>
            <p className="text-[11px] text-emerald-600 font-bold uppercase tracking-wider">Online to help</p>
          </div>
        </div>
        <div className="hidden sm:block text-right">
            <p className="text-[10px] text-slate-400 max-w-[180px] leading-tight">
                Keep the conversation focused on pet health and adoption logistics.
            </p>
        </div>
      </div>
      
      {/* 2. Messages Area */}
      <div className="flex-1 overflow-y-auto p-6 space-y-4 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] bg-fixed">
        {messages.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-center space-y-2">
            <div className="text-[10px] font-semibold uppercase tracking-wider text-stone-400">No messages</div>
            <p className="text-slate-400 text-sm italic">No messages yet. Say hi to {otherUserName}!</p>
          </div>
        ) : (
          messages.map((msg) => {
            const isMe = msg.senderId === currentUserId;
            return (
              <div
                key={msg.id}
                className={`flex ${isMe ? 'justify-end' : 'justify-start'} animate-page-fade`}
              >
                <div className={`max-w-[80%] md:max-w-[70%] group`}>
                  <div className={`
                    px-4 py-3 rounded-2xl text-sm shadow-sm
                    ${isMe 
                      ? 'bg-brand-500 text-white rounded-tr-none shadow-brand-200' 
                      : 'bg-slate-100 text-slate-700 rounded-tl-none border border-slate-200/50'}
                  `}>
                    {msg.content}
                  </div>
                  <p className={`text-[10px] mt-1 text-slate-400 font-medium ${isMe ? 'text-right' : 'text-left'}`}>
                    {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </p>
                </div>
              </div>
            );
          })
        )}
        <div ref={messagesEndRef} />
      </div>
      
      {/* 3. Input Bar */}
      <div className="p-4 border-t border-slate-100 bg-white">
        <div className="flex items-end gap-3 bg-slate-50 rounded-2xl p-2 border border-slate-200 focus-within:border-brand-400 focus-within:ring-2 focus-within:ring-brand-100 transition-all">
          <textarea
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Write a message..."
            className="flex-1 bg-transparent border-none outline-none text-sm p-2 min-h-[44px] max-h-[120px] resize-none text-slate-700"
            rows="1"
          />
          <button
            onClick={handleSend}
            disabled={!newMessage.trim()}
            className="bg-brand-500 hover:bg-brand-600 disabled:bg-slate-300 text-white p-3 rounded-xl transition-all shadow-lg shadow-brand-500/20 active:scale-95 flex items-center justify-center"
          >
            <svg className="w-5 h-5 rotate-90" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
}

export default ChatWindow;
