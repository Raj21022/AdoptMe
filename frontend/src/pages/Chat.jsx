import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, Link } from 'react-router-dom';
import ChatWindow from '../components/ChatWindow';
import api from '../services/api';

function Chat({ currentUser }) {
  const { userId } = useParams();
  const [searchParams] = useSearchParams();
  const [otherUser, setOtherUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!currentUser?.userId || !userId || currentUser.userId.toString() === userId.toString()) {
      setOtherUser(null);
      setLoading(false);
      return;
    }

    const nameFromQuery = searchParams.get('name');
    if (nameFromQuery) {
      setOtherUser({
        id: Number(userId),
        name: nameFromQuery
      });
      setLoading(false);
      return;
    }

    loadOtherUser();
  }, [userId, currentUser?.userId, searchParams]);

  const loadOtherUser = async () => {
    try {
      // Designers Note: Fetching all pets just to find a user is heavy, 
      // but keeping your logic intact while we focus on the UI!
      const response = await api.get(`/pets`);
      const pets = response.data;
      const pet = pets.find(p => p.listedById.toString() === userId);
      
      if (pet) {
        setOtherUser({
          id: pet.listedById,
          name: pet.listedByName
        });
      } else {
        setOtherUser({
          id: Number(userId),
          name: `Guardian #${userId}` // More "AdoptMe" themed than "User"
        });
      }
    } catch (error) {
      console.error('Error loading user:', error);
      setOtherUser({
        id: Number(userId),
        name: `Guardian #${userId}`
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-[70vh] flex flex-col items-center justify-center animate-pulse">
        <div className="w-16 h-16 bg-brand-100 rounded-full flex items-center justify-center mb-4">
          <span className="text-[10px] font-semibold uppercase tracking-wider text-brand-700">Chat</span>
        </div>
        <p className="text-slate-400 font-medium font-heading">Opening secure chat...</p>
      </div>
    );
  }

  if (!otherUser) {
    return (
      <div className="page-enter max-w-2xl mx-auto px-4 py-20 text-center">
        <div className="glass-card p-12 border-brand-100">
          <div className="text-[10px] font-semibold uppercase tracking-wider text-stone-400 mb-6">Unavailable</div>
          <h2 className="text-2xl font-heading mb-4">Chat Unavailable</h2>
          <p className="text-slate-500 mb-8 leading-relaxed">
            We couldn't establish a connection with this guardian. This usually happens if the link is outdated or you're trying to chat with yourself.
          </p>
          <Link to="/" className="btn-primary inline-block px-8">
            Return to Browse
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page-enter min-h-[calc(100vh-80px)] bg-slate-50/30">
      <div className="max-w-5xl mx-auto px-4 py-6 md:py-10">
        {/* Navigation Breadcrumb */}
        <div className="mb-6 flex items-center gap-2 text-sm">
          <Link to="/" className="text-slate-400 hover:text-brand-500 transition-colors">Browse</Link>
          <span className="text-slate-300">/</span>
          <span className="text-slate-600 font-semibold">Chat with {otherUser.name}</span>
        </div>

        <div className="shadow-2xl shadow-brand-900/5 rounded-3xl overflow-hidden bg-white border border-slate-100">
          <ChatWindow
            currentUserId={currentUser.userId}
            otherUserId={otherUser.id}
            otherUserName={otherUser.name}
          />
        </div>

        <p className="mt-4 text-center text-xs text-slate-400 flex items-center justify-center gap-1">
          <span className="text-[10px] font-semibold uppercase tracking-wider">Private</span> Your conversation is private and secure
        </p>
      </div>
    </div>
  );
}

export default Chat;
