G100 (MT6993) — Device / SoC / GPU specs and notes

SoC / Model
- Model number: MT6993 (updated)
- Announced: September 2025
- Class: Flagship
- Manufacturing: TSMC 3 nm

CPU
- Architecture: ARMv9.3-A
- Cores: 8 (1x C1-Ultra @ 4.21 GHz, 3x C1-Premium @ 3.5 GHz, 4x C1-Pro @ 2.7 GHz)
- L1 cache: 192 KB
- L2 cache: 2 MB
- L3 cache: 16 MB
- Process: 3 nm
- TDP (Sustained Power Limit): 9 W

GPU
- Name: Mali-G1 Ultra MP12
- Architecture: Valhall 5th gen
- GPU frequency: 1716 MHz
- Pipelines: 12
- Shading units: 128
- Total shaders: 1536
- FLOPS: 5271.5 GFLOPS
- Vulkan version: 1.4
- OpenCL version: 3.0
- DirectX version: 12.1

Notes / Feature requests / Bug summary
- Added framerate display (show 120 FPS)
- Battery life drain issues during gameplay: app remains running in background; wake-up notifications; background services
- Requested: disable wake-up notification, disable problematic optimizations, ensure gameplay mode is fully stopped when app exits, fix background running/battery drain
- Action items: ensure game-mode resources (wake locks, background services, notifications) are released on app background/exit; add FPS limiter and toggle in settings; provide an "allow background running" toggle (default off)

History
- Replaced references to MediaTek Dimensity 9500 with MT6993 / Mali-G1 Ultra MP12
