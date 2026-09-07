/**
 * Role Selection Card Component for Raitha Mitra.
 * 
 * @file src/components/common/UserTypeCard.tsx
 * @description Selectable visual card for choosing Farmer vs Labourer role with icon, title,
 * description, and high contrast active state.
 */

import React from 'react';
import { Tractor, Users, CheckCircle2 } from 'lucide-react';
import type { UserRole } from '../../types/role.types';
import { Card } from '../ui/Card';

export interface UserTypeCardProps {
  role: UserRole;
  title: string;
  description: string;
  selected: boolean;
  onSelect: (role: UserRole) => void;
}

export const UserTypeCard: React.FC<UserTypeCardProps> = ({
  role,
  title,
  description,
  selected,
  onSelect,
}) => {
  const IconComponent = role === 'FARMER' ? Tractor : Users;

  return (
    <Card
      selected={selected}
      clickable={true}
      onClick={() => onSelect(role)}
      className="p-5 flex flex-col justify-between space-y-4 text-left transition-transform active:scale-[0.98]"
    >
      <div className="flex items-start justify-between">
        <div
          className={`w-14 h-14 rounded-2xl flex items-center justify-center transition-colors ${
            selected
              ? 'bg-emerald-700 text-white shadow-sm'
              : 'bg-emerald-100 text-emerald-800'
          }`}
        >
          <IconComponent className="w-8 h-8" />
        </div>

        <div
          className={`w-6 h-6 rounded-full flex items-center justify-center transition-colors ${
            selected ? 'text-emerald-700' : 'text-slate-300'
          }`}
        >
          <CheckCircle2 className={`w-6 h-6 ${selected ? 'fill-emerald-100' : ''}`} />
        </div>
      </div>

      <div className="space-y-1.5">
        <h3 className="text-xl font-bold text-slate-900 leading-snug">{title}</h3>
        <p className="text-sm text-slate-600 leading-relaxed font-normal">{description}</p>
      </div>
    </Card>
  );
};
