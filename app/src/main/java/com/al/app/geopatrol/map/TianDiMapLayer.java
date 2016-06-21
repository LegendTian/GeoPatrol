package com.al.app.geopatrol.map;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.al.app.geopatrol.utils.CacheUtils;
import com.al.app.geopatrol.utils.HashUtils;
import com.esri.android.map.TiledServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Dai Jingjing on 2016/3/16.
 */
public class TianDiMapLayer extends TiledServiceLayer {

    private TianDiMapLayerType _mapType;
    private TileInfo tiandituTileInfo;
    private Context _context;

    public TianDiMapLayer(Context c) {
        this(c, null, null, true);
    }

    public TianDiMapLayer(Context c, TianDiMapLayerType mapType) {
        this(c, mapType, null, true);
    }

    public TianDiMapLayer(Context c, TianDiMapLayerType mapType, UserCredentials usercredentials) {
        this(c, mapType, usercredentials, true);
    }

    public TianDiMapLayer(Context c, TianDiMapLayerType mapType, UserCredentials usercredentials, boolean flag) {
        super("");
        this._context = c;
        this._mapType = mapType;
        setCredentials(usercredentials);

        if (flag)
            try {
                getServiceExecutor().submit(new Runnable() {

                    public final void run() {
                        a.initLayer();
                    }

                    final TianDiMapLayer a;


                    {
                        a = TianDiMapLayer.this;
                        //super();
                    }
                });
                return;
            } catch (RejectedExecutionException _ex) {
            }
    }

    protected Context getContext() {
        return _context;
    }

    public TianDiMapLayerType getMapType() {
        return this._mapType;
    }

    protected void initLayer() {
        this.buildTileInfo();
        this.setFullExtent(new Envelope(-180, -90, 180, 90));
        this.setDefaultSpatialReference(SpatialReference.create(4490));   //CGCS2000
        //this.setDefaultSpatialReference(SpatialReference.create(4326));
        this.setInitialExtent(new Envelope(90.52, 33.76, 113.59, 42.88));
        super.initLayer();
    }

    public void refresh() {
        try {
            getServiceExecutor().submit(new Runnable() {

                public final void run() {
                    if (a.isInitialized())
                        try {
                            a.b();
                            a.clearTiles();
                            return;
                        } catch (Exception exception) {
                            Log.e("ArcGIS", "Re-initialization of the layer failed.", exception);
                        }
                }

                final TianDiMapLayer a;

                {
                    a = TianDiMapLayer.this;
                    //super();
                }
            });
            return;
        } catch (RejectedExecutionException _ex) {
            return;
        }
    }

    final void b() throws Exception {
    }

    @Override
    protected byte[] getTile(int level, int col, int row) throws Exception {
        byte[] result = null;
        String cache_key = HashUtils.md5(String.format("TianDiMapTile_%d_%d_%d", level, col, row));
//        try {
//            result = CacheUtils.getBytes(getContext(), cache_key);
//        } catch (Exception e) {
//
//        }
//
//        if (result != null)
//            return result;

        for (int i = 0; i < 3 && result == null; i++) { // 重试下载3次
            try {
                result = downloadTile(level, col, row);
            } catch (Exception ex) {
                //ex.printStackTrace();
                Log.e("TiandiMapLayer", String.format("download tiandi map tile faild!(retry: %d)", i+1));
            }
        }

//        if (result != null && result.length > 0) {
//            try {
//                CacheUtils.put(getContext(), cache_key, result);
//            } catch (Exception e) {
//            }
//        }
        return result;
    }

    private byte[] downloadTile(int level, int col, int row) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        URL url = new URL(this.getTianDiMapUrl(level, col, row));
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        byte[] buf = new byte[1024];

        httpUrl = (HttpURLConnection) url.openConnection();
        httpUrl.connect();
        bis = new BufferedInputStream(httpUrl.getInputStream());

        while (true) {
            int bytes_read = bis.read(buf);
            if (bytes_read > 0) {
                bos.write(buf, 0, bytes_read);
            } else {
                break;
            }
        }

        bis.close();
        httpUrl.disconnect();

        return bos.toByteArray();
    }


    @Override
    public TileInfo getTileInfo() {
        return this.tiandituTileInfo;
    }

    /**
     *
     * */
    private String getTianDiMapUrl(int level, int col, int row) {
        return new TianDiMapUrl(level, col, row, this._mapType).generatUrl();
    }

    private void buildTileInfo() {
        Point originalPoint = new Point(-180, 90);

        double[] res = {
                1.40625,
                0.703125,
                0.3515625,
                0.17578125,
                0.087890625,
                0.0439453125,
                0.02197265625,
                0.010986328125,
                0.0054931640625,
                0.00274658203125,
                0.001373291015625,
                0.0006866455078125,
                0.00034332275390625,
                0.000171661376953125,
                8.58306884765629E-05,
                4.29153442382814E-05,
                2.14576721191407E-05,
                1.07288360595703E-05,
                5.36441802978515E-06,
                2.68220901489258E-06,
                1.34110450744629E-06
        };
        double[] scale = {
                400000000,
                295497598.5708346,
                147748799.285417,
                73874399.6427087,
                36937199.8213544,
                18468599.9106772,
                9234299.95533859,
                4617149.97766929,
                2308574.98883465,
                1154287.49441732,
                577143.747208662,
                288571.873604331,
                144285.936802165,
                72142.9684010827,
                36071.4842005414,
                18035.7421002707,
                9017.87105013534,
                4508.93552506767,
                2254.467762533835,
                1127.2338812669175,
                563.616940
        };
        int levels = 21;
        int dpi = 96;
        int tileWidth = 256;
        int tileHeight = 256;
        this.tiandituTileInfo = new com.esri.android.map.TiledServiceLayer.TileInfo(originalPoint, scale, res, levels, dpi, tileWidth, tileHeight);
        this.setTileInfo(this.tiandituTileInfo);
    }
}
