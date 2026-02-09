import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { deletePet, getPetById, updatePet } from '../services/pet';

const PLACEHOLDER_IMAGE = `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400" viewBox="0 0 600 400"><rect width="600" height="400" fill="#f8fafc"/><text x="300" y="200" text-anchor="middle" font-family="Quicksand" font-size="20" fill="#cbd5e1">Image coming soon</text></svg>'
)}`;

function PetDetails({ user }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pet, setPet] = useState(null);
  const [activeImage, setActiveImage] = useState(PLACEHOLDER_IMAGE);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');
  const [statusValue, setStatusValue] = useState('AVAILABLE');
  const [statusUpdating, setStatusUpdating] = useState(false);
  const statusLabel = pet?.adoptionStatus || 'AVAILABLE';

  const images = useMemo(() => {
    if (!pet || !Array.isArray(pet.imageUrls) || pet.imageUrls.length === 0) {
      return [PLACEHOLDER_IMAGE];
    }
    return pet.imageUrls;
  }, [pet]);

  const canManage = Boolean(
    pet &&
    (user?.role === 'COMMON_LISTER' || user?.role === 'NGO_LISTER') &&
    user?.userId === pet.listedById
  );

  const canChat = Boolean(pet && user?.userId && user.userId !== pet.listedById);

  useEffect(() => {
    const loadPet = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await getPetById(id);
        setPet(response);
      } catch (err) {
        setError(err.response?.data?.error || 'Failed to load pet details');
      } finally {
        setLoading(false);
      }
    };
    loadPet();
  }, [id]);

  useEffect(() => {
    if (images.length > 0) setActiveImage(images[0]);
  }, [images]);

  useEffect(() => {
    if (pet?.adoptionStatus) {
      setStatusValue(pet.adoptionStatus);
    }
  }, [pet]);

  const handleChat = () => {
    if (!pet) return;
    const targetName = encodeURIComponent(pet.listedByName || 'Lister');
    navigate(`/chat/${pet.listedById}?name=${targetName}`);
  };

  const handleEdit = () => navigate(`/edit-pet/${pet.id}`);

  const handleDelete = async () => {
    if (!pet) return;
    if (!window.confirm('Are you sure? This listing will be gone forever.')) return;
    setDeleting(true);
    try {
      await deletePet(pet.id);
      navigate('/');
    } catch (err) {
      setError('Failed to delete pet');
    } finally {
      setDeleting(false);
    }
  };

  const handleStatusUpdate = async () => {
    if (!pet) return;
    setStatusUpdating(true);
    setError('');
    try {
      const payload = {
        name: pet.name,
        age: pet.age,
        type: pet.type,
        description: pet.description,
        contactNumber: pet.contactNumber,
        location: pet.location,
        landmark: pet.landmark || '',
        locationLink: pet.locationLink || '',
        vaccinationStatus: pet.vaccinationStatus || 'Unknown',
        stray: Boolean(pet.stray),
        adoptionStatus: statusValue,
        imageUrls: Array.isArray(pet.imageUrls) ? pet.imageUrls : []
      };
      const updated = await updatePet(pet.id, payload);
      setPet(updated);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to update status');
    } finally {
      setStatusUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-12 h-12 border-4 border-brand-200 border-t-brand-600 rounded-full animate-spin"></div>
      </div>
    );
  }

  if (!pet) return <div className="p-20 text-center font-heading text-slate-500">Pet not found.</div>;

  return (
    <div className="page-enter pb-24">
      {/* 1. Breadcrumb & Header */}
      <div className="max-w-7xl mx-auto px-4 pt-8 md:pt-12">
        <Link to="/" className="text-sm font-bold text-brand-500 hover:underline mb-4 inline-block">
           Back to Search
        </Link>
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-8">
          <div>
            <h1 className="text-4xl md:text-5xl font-heading font-bold text-slate-900 mb-2">{pet.name}</h1>
            <p className="text-lg text-slate-500 flex items-center gap-2">
              <span className="bg-slate-100 px-3 py-1 rounded-full text-sm font-bold uppercase tracking-wider">{pet.type}</span>
              <span>in {pet.location}</span>
            </p>
            <div className="mt-2 flex flex-wrap items-center gap-2 text-xs">
              <span className={`px-3 py-1 rounded-lg font-semibold uppercase tracking-wider ${
                statusLabel === 'ADOPTED'
                  ? 'bg-stone-200 text-stone-700'
                  : statusLabel === 'PENDING'
                  ? 'bg-warm-100 text-warm-700'
                  : 'bg-brand-100 text-brand-700'
              }`}>
                {statusLabel}
              </span>
              <span className={`px-3 py-1 rounded-lg font-semibold uppercase tracking-wider ${
                pet.stray ? 'bg-red-50 text-red-600' : 'bg-stone-100 text-stone-600'
              }`}>
                {pet.stray ? 'Stray' : 'Not stray'}
              </span>
            </div>
          </div>
          {canManage && (
            <div className="flex gap-2">
              <button onClick={handleEdit} className="px-6 py-2 border-2 border-slate-200 rounded-xl font-bold text-slate-600 hover:bg-slate-50 transition-colors">
                Edit Listing
              </button>
              <button onClick={handleDelete} disabled={deleting} className="px-6 py-2 border-2 border-red-100 text-red-500 rounded-xl font-bold hover:bg-red-50 transition-colors">
                {deleting ? '...' : 'Remove'}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* 2. Main Content Grid */}
      <div className="max-w-7xl mx-auto px-4 grid grid-cols-1 lg:grid-cols-12 gap-10">
        
        {/* Left: Gallery */}
        <div className="lg:col-span-7 space-y-4">
          <div className="aspect-[4/3] rounded-3xl overflow-hidden bg-slate-100 shadow-inner">
            <img 
              src={activeImage} 
              alt={pet.name} 
              className="w-full h-full object-cover transition-all duration-500"
            />
          </div>
          {images.length > 1 && (
            <div className="flex gap-3 overflow-x-auto pb-2 no-scrollbar">
              {images.map((img, i) => (
                <button 
                  key={i} 
                  onClick={() => setActiveImage(img)}
                  className={`relative min-w-[100px] h-20 rounded-xl overflow-hidden border-2 transition-all 
                    ${activeImage === img ? 'border-brand-500 scale-95' : 'border-transparent opacity-70 hover:opacity-100'}`}
                >
                  <img src={img} className="w-full h-full object-cover" alt="" />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right: Info Sidebar */}
        <div className="lg:col-span-5 space-y-6">
          <div className="glass-card p-8 border-brand-50 shadow-xl shadow-brand-500/5">
            <h3 className="text-xl font-heading font-bold mb-6 text-slate-800">Pet Details</h3>
            
            <div className="grid grid-cols-2 gap-4 mb-8">
              <div className="p-4 bg-brand-50/50 rounded-xl">
                <p className="text-[10px] font-bold text-brand-500 uppercase tracking-widest mb-1">Age</p>
                <p className="text-lg font-bold text-slate-800">{pet.age ? `${pet.age} Years` : 'Unknown'}</p>
              </div>
              <div className="p-4 bg-warm-50/50 rounded-xl">
                <p className="text-[10px] font-bold text-warm-600 uppercase tracking-widest mb-1">Vaccination</p>
                <p className="text-lg font-bold text-slate-800">{pet.vaccinationStatus || 'Unknown'}</p>
              </div>
              <div className="p-4 bg-stone-50 rounded-xl">
                <p className="text-[10px] font-bold text-stone-500 uppercase tracking-widest mb-1">Phone</p>
                <a href={`tel:${pet.contactNumber}`} className="text-lg font-bold text-slate-800">
                  {pet.contactNumber || 'N/A'}
                </a>
              </div>
              <div className="p-4 bg-stone-50 rounded-xl">
                <p className="text-[10px] font-bold text-stone-500 uppercase tracking-widest mb-1">Landmark</p>
                <p className="text-lg font-bold text-slate-800 truncate">{pet.landmark || 'N/A'}</p>
              </div>
            </div>

            <div className="space-y-2 mb-8">
              <p className="text-sm font-semibold text-stone-600">Exact Location</p>
              {pet.locationLink ? (
                <a
                  href={pet.locationLink}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-2 text-brand-600 font-semibold hover:underline"
                >
                  Open in Maps
                </a>
              ) : (
                <p className="text-sm text-stone-400">No maps link provided.</p>
              )}
            </div>

            <div className="space-y-4 mb-8">
              <h4 className="font-bold text-slate-800">Meet {pet.name}</h4>
              <p className="text-slate-600 leading-relaxed italic">
                "{pet.description || "The lister hasn't provided a description yet, but reach out to learn more about this lovely companion!"}"
              </p>
            </div>

            <div className="pt-6 border-t border-slate-100">
              <div className="flex items-center gap-4 mb-6">
                <div className="w-12 h-12 bg-slate-200 rounded-full flex items-center justify-center font-bold text-slate-500">
                  {pet.listedByName?.charAt(0)}
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Listed By</p>
                  <p className="font-bold text-slate-800">{pet.listedByName}</p>
                </div>
              </div>

              {canManage && (
                <div className="mb-6 space-y-2">
                  <label className="text-xs font-bold uppercase tracking-widest text-slate-400">Update Adoption Status</label>
                  <div className="flex items-center gap-2">
                    <select
                      className="input-field"
                      value={statusValue}
                      onChange={(e) => setStatusValue(e.target.value)}
                    >
                      <option value="AVAILABLE">Available</option>
                      <option value="PENDING">Pending</option>
                      <option value="ADOPTED">Adopted</option>
                    </select>
                    <button
                      type="button"
                      onClick={handleStatusUpdate}
                      disabled={statusUpdating}
                      className="btn-secondary px-4"
                    >
                      {statusUpdating ? 'Saving...' : 'Save'}
                    </button>
                  </div>
                </div>
              )}

              {canChat ? (
                <button 
                  onClick={handleChat}
                  className="w-full btn-primary py-4 text-lg flex items-center justify-center gap-3 shadow-lg shadow-brand-500/30"
                >
                  Message {pet.listedByName}
                </button>
              ) : !user ? (
                <Link to="/login" className="w-full btn-primary py-4 block text-center">
                  Login to Adopt
                </Link>
              ) : null}
            </div>
          </div>

          <div className="p-6 bg-slate-900 rounded-3xl text-white">
            <h4 className="font-bold mb-2 flex items-center gap-2">
              <span className="text-[10px] font-semibold uppercase tracking-wider text-brand-200">Safety</span> Adoption Safety
            </h4>
            <p className="text-xs text-slate-400 leading-relaxed">
              Always meet in public places and never send money before meeting the pet and the lister in person.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PetDetails;
