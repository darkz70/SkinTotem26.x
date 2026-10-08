#!/usr/bin/env python3
"""
Procedural sound generator for SkinTotem.

Every sound shipped in assets/skintotem/sounds/ is synthesized by this script,
so the audio contains no third-party samples and can be regenerated/tweaked at
any time.

Usage:
    pip install numpy soundfile
    python3 tools/gen_sounds.py

Output: src/main/resources/assets/skintotem/sounds/*.ogg (mono, 44.1 kHz, Vorbis)
"""

from __future__ import annotations

import pathlib

import numpy as np
import soundfile as sf

SR = 44_100
OUT = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/skintotem/sounds"


def t(duration: float) -> np.ndarray:
    return np.linspace(0.0, duration, int(SR * duration), endpoint=False)


def env_exp(time: np.ndarray, attack: float, decay: float) -> np.ndarray:
    """Fast attack + exponential decay envelope."""
    attack_samples = max(int(SR * attack), 1)
    envelope = np.exp(-time / decay)
    envelope[:attack_samples] *= np.linspace(0.0, 1.0, attack_samples)
    return envelope


def lowpass(signal: np.ndarray, cutoff: float) -> np.ndarray:
    """One-pole lowpass, enough for taking the harshness off noise layers."""
    alpha = 1.0 - np.exp(-2.0 * np.pi * cutoff / SR)
    out = np.empty_like(signal)
    acc = 0.0
    for i, sample in enumerate(signal):
        acc += alpha * (sample - acc)
        out[i] = acc
    return out


def finish(signal: np.ndarray, peak: float = 0.82) -> np.ndarray:
    """Normalize and de-click the edges."""
    signal = np.nan_to_num(signal)
    maximum = float(np.max(np.abs(signal)))
    if maximum > 0:
        signal = signal / maximum * peak
    fade = min(int(SR * 0.006), len(signal) // 2)
    if fade > 0:
        signal[:fade] *= np.linspace(0.0, 1.0, fade)
        signal[-fade:] *= np.linspace(1.0, 0.0, fade)
    return signal.astype(np.float32)


def bell(time: np.ndarray, freq: float, decay: float, partials=(1.0, 2.01, 2.99, 4.21)) -> np.ndarray:
    """Inharmonic additive bell."""
    out = np.zeros_like(time)
    for index, ratio in enumerate(partials):
        amplitude = 1.0 / (index + 1) ** 1.35
        out += amplitude * np.sin(2 * np.pi * freq * ratio * time) * np.exp(-time / (decay / (index * 0.45 + 1)))
    return out


def totem_activate() -> np.ndarray:
    """Magic shimmer burst for the moment the totem saves the player."""
    time = t(1.9)
    chord = (
        bell(time, 523.25, 0.95)            # C5
        + 0.85 * bell(time, 659.25, 0.80)   # E5
        + 0.70 * bell(time, 783.99, 0.70)   # G5
        + 0.55 * bell(time, 1046.50, 0.55)  # C6
    )

    # rising shimmer right at the start
    sweep_env = env_exp(time, 0.004, 0.22)
    sweep = np.sin(2 * np.pi * (440 + 2600 * np.clip(time / 0.35, 0, 1) ** 1.6) * time) * sweep_env * 0.35

    # airy sparkle tail
    rng = np.random.default_rng(2026_03)
    sparkle = lowpass(rng.normal(0, 1, len(time)), 5200) * np.exp(-time / 0.5) * 0.22
    tremolo = 0.75 + 0.25 * np.sin(2 * np.pi * 9.0 * time)

    return finish((chord * env_exp(time, 0.003, 0.85) + sweep + sparkle * tremolo) * 0.6)


def doll_summon() -> np.ndarray:
    """Short pop + whoosh for the doll appearing in hand."""
    time = t(0.55)
    pitch = 180 + 520 * (1 - np.exp(-time / 0.07))
    phase = 2 * np.pi * np.cumsum(pitch) / SR
    body = np.sin(phase) * env_exp(time, 0.002, 0.11)
    click = np.sin(2 * np.pi * 1400 * time) * env_exp(time, 0.001, 0.02) * 0.5

    rng = np.random.default_rng(7)
    air = lowpass(rng.normal(0, 1, len(time)), 2600) * np.exp(-((time - 0.06) ** 2) / 0.0035) * 0.4

    return finish(body + click + air)


def skin_change() -> np.ndarray:
    """Two-tone confirmation blip for a successful /skintotem command."""
    time = t(0.34)
    first = np.sin(2 * np.pi * 880 * time) * env_exp(time, 0.002, 0.055)
    second_start = int(SR * 0.085)
    second = np.zeros_like(time)
    tail = time[: len(time) - second_start]
    second[second_start:] = np.sin(2 * np.pi * 1318.5 * tail) * env_exp(tail, 0.002, 0.085)
    harmonic = 0.18 * np.sin(2 * np.pi * 2637 * time) * env_exp(time, 0.002, 0.04)
    return finish(first * 0.7 + second + harmonic)


def skin_error() -> np.ndarray:
    """Soft descending buzz for a failed skin load."""
    time = t(0.52)
    pitch = 330 * np.exp(-time * 1.9) + 110
    phase = 2 * np.pi * np.cumsum(pitch) / SR
    tone = (np.sin(phase) + 0.35 * np.sin(2 * phase) + 0.18 * np.sin(3 * phase))
    buzz = np.sign(np.sin(phase * 0.5)) * 0.12
    return finish((tone + buzz) * env_exp(time, 0.006, 0.19))


SOUNDS = {
    "totem_activate": totem_activate,
    "doll_summon": doll_summon,
    "skin_change": skin_change,
    "skin_error": skin_error,
}


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    for name, generator in SOUNDS.items():
        data = generator()
        path = OUT / f"{name}.ogg"
        sf.write(path, data, SR, format="OGG", subtype="VORBIS")
        print(f"{path.name:<22} {len(data) / SR:4.2f}s  {path.stat().st_size / 1024:5.1f} KiB")


if __name__ == "__main__":
    main()
