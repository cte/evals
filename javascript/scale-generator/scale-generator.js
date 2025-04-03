export class Scale {
  constructor(tonic) {
    this.tonic = tonic[0].toUpperCase() + tonic.slice(1).toLowerCase();

    this.flatTonics = [
      'F', 'Bb', 'Eb', 'Ab', 'Db', 'Gb',
      'd', 'g', 'c', 'f', 'bb', 'eb'
    ];

    this.sharpNotes = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B'];
    this.flatNotes  = ['C', 'Db', 'D', 'Eb', 'E', 'F', 'Gb', 'G', 'Ab', 'A', 'Bb', 'B'];

    this.useFlats = this.flatTonics.includes(tonic) || this.flatTonics.includes(this.tonic);

    this.notes = this.useFlats ? this.flatNotes : this.sharpNotes;
  }

  chromatic() {
    const idx = this.notes.findIndex(n => n.toLowerCase() === this.tonic.toLowerCase());
    return [...this.notes.slice(idx), ...this.notes.slice(0, idx)];
  }

  interval(intervals) {
    const scale = [];
    const chromatic = this.chromatic();
    let idx = 0;
    scale.push(chromatic[idx]);

    for (const step of intervals) {
      if (step === 'm') {
        idx += 1;
      } else if (step === 'M') {
        idx += 2;
      } else if (step === 'A') {
        idx += 3;
      } else {
        throw new Error(`Invalid interval character: ${step}`);
      }
      idx = idx % chromatic.length;
      scale.push(chromatic[idx]);
    }

    return scale;
  }
}
