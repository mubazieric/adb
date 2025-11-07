const PHONE_REGEX = /^\+?[1-9]\d{7,14}$/;

export const validatePhoneNumber = (value: string): boolean => PHONE_REGEX.test(value);

export const estimateSmsSegments = (message: string): number => {
  const GSM_7BIT_LIMIT = 160;
  const GSM_7BIT_CONCAT_LIMIT = 153;
  const unicode = /[^\x00-\x7F]/.test(message);
  if (message.length === 0) {
    return 0;
  }
  if (unicode) {
    const UCS2_LIMIT = 70;
    const UCS2_CONCAT_LIMIT = 67;
    return message.length <= UCS2_LIMIT
      ? 1
      : Math.ceil(message.length / UCS2_CONCAT_LIMIT);
  }
  return message.length <= GSM_7BIT_LIMIT
    ? 1
    : Math.ceil(message.length / GSM_7BIT_CONCAT_LIMIT);
};
