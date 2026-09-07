/**
 * User Role domain types for Raitha Mitra.
 * 
 * @file src/types/role.types.ts
 * @description Defines application-level user role identifiers and role metadata contracts.
 */

export type UserRole = 'FARMER' | 'LABOURER';

export interface UserRoleOption {
  id: UserRole;
  iconName: string;
  translationKey: string; // Key prefix for i18n localization lookup
}
