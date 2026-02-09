import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { getInbox } from '../services/chat';

function Inbox() {
  const navigate = useNavigate();
  const [conversations, setConversations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadInbox = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await getInbox();
        setConversations(data);
      } catch (err) {
        setError(err.response?.data?.error || 'Failed to load inbox');
      } finally {
        setLoading(false);
      }
    };

    loadInbox();
  }, []);

  const openConversation = (conversation) => {
    const encodedName = encodeURIComponent(conversation.otherUserName || 'User');
    navigate(`/chat/${conversation.otherUserId}?name=${encodedName}`);
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const now = new Date();
    // If today, show time, otherwise show date
    if (date.toDateString() === now.toDateString()) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
  };

  if (loading) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center">
        <div className="w-10 h-10 border-4 border-brand-100 border-t-brand-500 rounded-full animate-spin mb-4"></div>
        <p className="text-slate-400 font-medium">Checking messages...</p>
      </div>
    );
  }

  return (
    <div className="page-enter max-w-4xl mx-auto px-4 py-8 md:py-12">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-heading font-bold text-slate-900">Messages</h1>
          <p className="text-slate-500">Adoption inquiries and guardian chats</p>
        </div>
        <div className="text-xs font-bold text-brand-500 bg-brand-50 px-3 py-1 rounded-full uppercase tracking-widest">
          {conversations.length} Active
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-100 text-red-600 p-4 rounded-2xl mb-6 text-sm">
          {error}
        </div>
      )}

      {conversations.length === 0 ? (
        <div className="glass-card p-16 text-center">
          <div className="text-[10px] font-semibold uppercase tracking-wider text-stone-400 mb-4">Messages</div>
          <h2 className="text-xl font-heading font-bold text-slate-800 mb-2">Inbox Empty</h2>
          <p className="text-slate-500 mb-8 max-w-xs mx-auto">
            Once you reach out to a pet guardian, your conversations will appear here.
          </p>
          <Link to="/" className="btn-primary px-8 inline-block">Find a Friend</Link>
        </div>
      ) : (
        <div className="space-y-3">
          {conversations.map((conversation) => (
            <button
              key={conversation.otherUserId}
              onClick={() => openConversation(conversation)}
              className="w-full text-left glass-card p-4 md:p-6 flex items-center gap-4 hover:border-brand-200 hover:shadow-lg transition-all active:scale-[0.99] group"
            >
              {/* User Avatar */}
              <div className="w-12 h-12 md:w-14 md:h-14 bg-brand-100 rounded-2xl flex items-center justify-center text-brand-600 font-bold text-lg group-hover:bg-brand-500 group-hover:text-white transition-colors">
                {conversation.otherUserName?.charAt(0) || 'U'}
              </div>

              {/* Message Content */}
              <div className="flex-1 min-w-0">
                <div className="flex justify-between items-baseline mb-1">
                  <h3 className="font-bold text-slate-800 truncate pr-4">
                    {conversation.otherUserName}
                  </h3>
                  <span className="text-[11px] font-bold text-slate-400 uppercase whitespace-nowrap">
                    {formatTime(conversation.lastMessageAt)}
                  </span>
                </div>
                <p className="text-sm text-slate-500 truncate italic">
                  {conversation.lastMessage || "No messages yet"}
                </p>
              </div>

              {/* Arrow Indicator */}
              <div className="text-slate-200 group-hover:text-brand-300 transition-colors">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
                </svg>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default Inbox;
