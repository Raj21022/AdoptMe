import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { deletePet } from '../services/pet';

const PLACEHOLDER_IMAGE = `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400" viewBox="0 0 600 400"><rect width="600" height="400" fill="#f1f5f9"/><path d="M300 170a40 40 0 100 80 40 40 0 000-80z" fill="#cbd5e1"/><text x="300" y="280" text-anchor="middle" font-family="Quicksand, sans-serif" font-weight="bold" font-size="20" fill="#94a3b8">Waiting for a photo...</text></svg>'
)}`;

function PetCard({ pet, currentUser, onPetDeleted }) {
  const navigate = useNavigate();
  const [deleting, setDeleting] = useState(false);

  const firstImage = Array.isArray(pet.imageUrls) && pet.imageUrls.length > 0
    ? pet.imageUrls[0]
    : (typeof pet.imageUrl === 'string' ? pet.imageUrl : null);
  
  const imageCount = Array.isArray(pet.imageUrls) ? pet.imageUrls.length : (firstImage ? 1 : 0);
  
  const canManage = Boolean(
    (currentUser?.role === 'COMMON_LISTER' || currentUser?.role === 'NGO_LISTER') &&
    currentUser?.userId === pet.listedById
  );
  
  const isOwner = Boolean(currentUser?.userId && currentUser.userId === pet.listedById);
  const canChat = Boolean(currentUser?.userId && pet.listedById && !isOwner);
  const isNGO = pet.listedByRole === 'NGO_LISTER';
  const adoptionStatus = pet.adoptionStatus || 'AVAILABLE';
  const isStray = pet.stray === true;

  const handleChat = (e) => {
    e.stopPropagation();
    if (canChat && pet.listedById) {
      const targetName = encodeURIComponent(pet.listedByName || 'Lister');
      navigate(`/chat/${pet.listedById}?name=${targetName}`);
    }
  };

  const handleOpenDetails = () => navigate(`/pets/${pet.id}`);
  const handleEdit = (e) => { e.stopPropagation(); navigate(`/edit-pet/${pet.id}`); };

  const handleDelete = async (e) => {
    e.stopPropagation();
    if (!window.confirm('Remove this listing?')) return;
    setDeleting(true);
    try {
      await deletePet(pet.id);
      if (onPetDeleted) onPetDeleted(pet.id);
    } catch (err) {
      window.alert('Failed to delete pet');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <article 
      onClick={handleOpenDetails}
      className="group glass-card overflow-hidden hover:shadow-xl hover:shadow-brand-500/10 transition-all duration-300 cursor-pointer flex flex-col h-full border-transparent hover:border-brand-100"
    >
      {/* 1. Image Area */}
      <div className="relative aspect-[4/3] overflow-hidden">
        <img
          src={firstImage || PLACEHOLDER_IMAGE}
          alt={pet.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        />
        
        <div className="absolute top-3 left-3 flex flex-wrap gap-2">
          {adoptionStatus && (
            <span className={`px-2.5 py-1 rounded-lg text-[10px] font-semibold uppercase tracking-wider ${
              adoptionStatus === 'ADOPTED'
                ? 'bg-stone-200 text-stone-700'
                : adoptionStatus === 'PENDING'
                ? 'bg-warm-100 text-warm-700'
                : 'bg-brand-100 text-brand-700'
            }`}>
              {adoptionStatus}
            </span>
          )}
          <span className={`px-2.5 py-1 rounded-lg text-[10px] font-semibold uppercase tracking-wider ${
            isStray ? 'bg-red-50 text-red-600' : 'bg-stone-100 text-stone-600'
          }`}>
            {isStray ? 'Stray' : 'Not stray'}
          </span>
        </div>

        {/* NGO Badge overlay */}
        {isNGO && (
          <div className="absolute top-3 right-3 bg-white/90 backdrop-blur px-3 py-1 rounded-lg shadow-sm flex items-center gap-1.5">
            <span className="text-[10px] font-bold text-brand-600 uppercase tracking-wider">NGO Verified</span>
          </div>
        )}

        {imageCount > 1 && (
          <div className="absolute bottom-3 right-3 bg-black/50 backdrop-blur-md text-white px-2.5 py-1 rounded-lg text-xs font-bold">
            +{imageCount - 1} Photos
          </div>
        )}
      </div>

      {/* 2. Content Area */}
      <div className="p-5 flex flex-col flex-grow">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-xl font-heading font-bold text-slate-800">{pet.name}</h3>
          <span className="text-xs font-bold px-2 py-1 bg-slate-100 text-slate-600 rounded uppercase">
            {pet.type}
          </span>
        </div>

        <div className="space-y-1.5 mb-4 text-sm text-slate-500">
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-stone-400">Location</span>
            <span className="text-stone-600">{pet.location || 'Unknown Location'}</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-stone-400">Age</span>
            <span className="text-stone-600">{pet.age ? `${pet.age} Years Old` : 'Age unknown'}</span>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-semibold uppercase tracking-wider text-stone-400">Listed by</span>
            <span className="text-stone-700 font-medium">{pet.listedByName}</span>
          </div>
        </div>

        {/* 3. Actions Row */}
        <div className="mt-auto pt-4 border-t border-slate-50 flex items-center gap-2">
          {currentUser?.userId ? (
            <button 
              onClick={canChat ? handleChat : handleOpenDetails}
              disabled={!canChat && isOwner}
              className={`flex-1 py-2.5 text-sm flex items-center justify-center gap-2 transition-colors ${
                canChat
                  ? 'btn-primary'
                  : isOwner
                  ? 'bg-stone-100 text-stone-500 font-semibold rounded-xl cursor-not-allowed'
                  : 'bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-xl'
              }`}
              title={isOwner ? 'You cannot message your own listing' : 'Message lister'}
            >
              {isOwner ? 'Your listing' : 'Message'}
            </button>
          ) : (
            <button 
              onClick={handleOpenDetails}
              className="flex-1 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-xl text-sm transition-colors"
            >
              View Details
            </button>
          )}

          {canManage && (
            <div className="flex gap-2">
              <button 
                onClick={handleEdit}
                className="p-2.5 bg-brand-50 text-brand-600 rounded-xl hover:bg-brand-100 transition-colors"
                title="Edit Listing"
              >
                
              </button>
              <button 
                onClick={handleDelete}
                disabled={deleting}
                className="p-2.5 bg-red-50 text-red-600 rounded-xl hover:bg-red-100 transition-colors"
                title="Delete Listing"
              >
                {deleting ? '...' : ''}
              </button>
            </div>
          )}
        </div>
      </div>
    </article>
  );
}

export default PetCard;
