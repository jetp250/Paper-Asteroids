# Paper-Asteroids
The old but cool asteroids game for 1.15.1 Paper Minecraft servers!

[Imgur video (too large to embed)](https://i.imgur.com/2q6Qnjm.mp4)

Built on the fact that maps are practically 128x128 server-editable images. Every tick, the canvas is cleared, rererendered with a simple line rasterizer, split into maps and sent to the clients. Because of the bandwidth requirements, it probably won't scale to more than a handful of players, especially if the maps are big :)
