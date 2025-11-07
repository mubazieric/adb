import { TextStyle } from 'react-native';
import { palette } from './colors';

export const typography = {
  display: {
    fontSize: 26,
    fontWeight: '700' as TextStyle['fontWeight'],
    color: palette.textPrimary,
  },
  title: {
    fontSize: 20,
    fontWeight: '600' as TextStyle['fontWeight'],
    color: palette.textPrimary,
  },
  body: {
    fontSize: 16,
    color: palette.textSecondary,
  },
  caption: {
    fontSize: 12,
    color: palette.textMuted,
    letterSpacing: 0.5,
  },
};
