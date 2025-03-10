/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.List;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.RTMPState;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

public class IngestServerTester {
    protected IngestTestListener field_153044_b = null;
    protected Stream field_153045_c = null;
    protected IngestList field_153046_d = null;
    protected IngestTestState field_153047_e = IngestTestState.Uninitalized;
    protected long field_153048_f = 8000L;
    protected long field_153049_g = 2000L;
    protected long field_153050_h = 0L;
    protected RTMPState field_153051_i = RTMPState.Invalid;
    protected VideoParams field_153052_j = null;
    protected AudioParams audioParameters = null;
    protected long field_153054_l = 0L;
    protected List<FrameBuffer> field_153055_m = null;
    protected boolean field_153056_n = false;
    protected IStreamCallbacks field_153057_o = null;
    protected IStatCallbacks field_153058_p = null;
    protected IngestServer field_153059_q = null;
    protected boolean field_153060_r = false;
    protected boolean field_153061_s = false;
    protected int field_153062_t = -1;
    protected int field_153063_u = 0;
    protected long field_153064_v = 0L;
    protected float field_153065_w = 0.0f;
    protected float field_153066_x = 0.0f;
    protected boolean field_176009_x = false;
    protected boolean field_176008_y = false;
    protected boolean field_176007_z = false;
    protected IStreamCallbacks field_176005_A = new IStreamCallbacks(){

        @Override
        public void requestAuthTokenCallback(ErrorCode p_requestAuthTokenCallback_1_, AuthToken p_requestAuthTokenCallback_2_) {
        }

        @Override
        public void loginCallback(ErrorCode p_loginCallback_1_, ChannelInfo p_loginCallback_2_) {
        }

        @Override
        public void getIngestServersCallback(ErrorCode p_getIngestServersCallback_1_, IngestList p_getIngestServersCallback_2_) {
        }

        @Override
        public void getUserInfoCallback(ErrorCode p_getUserInfoCallback_1_, UserInfo p_getUserInfoCallback_2_) {
        }

        @Override
        public void getStreamInfoCallback(ErrorCode p_getStreamInfoCallback_1_, StreamInfo p_getStreamInfoCallback_2_) {
        }

        @Override
        public void getArchivingStateCallback(ErrorCode p_getArchivingStateCallback_1_, ArchivingState p_getArchivingStateCallback_2_) {
        }

        @Override
        public void runCommercialCallback(ErrorCode p_runCommercialCallback_1_) {
        }

        @Override
        public void setStreamInfoCallback(ErrorCode p_setStreamInfoCallback_1_) {
        }

        @Override
        public void getGameNameListCallback(ErrorCode p_getGameNameListCallback_1_, GameInfoList p_getGameNameListCallback_2_) {
        }

        @Override
        public void bufferUnlockCallback(long p_bufferUnlockCallback_1_) {
        }

        @Override
        public void startCallback(ErrorCode p_startCallback_1_) {
            IngestServerTester.this.field_176008_y = false;
            if (ErrorCode.succeeded(p_startCallback_1_)) {
                IngestServerTester.this.field_176009_x = true;
                IngestServerTester.this.field_153054_l = System.currentTimeMillis();
                IngestServerTester.this.func_153034_a(IngestTestState.ConnectingToServer);
            } else {
                IngestServerTester.this.field_153056_n = false;
                IngestServerTester.this.func_153034_a(IngestTestState.DoneTestingServer);
            }
        }

        @Override
        public void stopCallback(ErrorCode p_stopCallback_1_) {
            if (ErrorCode.failed(p_stopCallback_1_)) {
                System.out.println("IngestTester.stopCallback failed to stop - " + IngestServerTester.this.field_153059_q.serverName + ": " + p_stopCallback_1_.toString());
            }
            IngestServerTester.this.field_176007_z = false;
            IngestServerTester.this.field_176009_x = false;
            IngestServerTester.this.func_153034_a(IngestTestState.DoneTestingServer);
            IngestServerTester.this.field_153059_q = null;
            if (IngestServerTester.this.field_153060_r) {
                IngestServerTester.this.func_153034_a(IngestTestState.Cancelling);
            }
        }

        @Override
        public void sendActionMetaDataCallback(ErrorCode p_sendActionMetaDataCallback_1_) {
        }

        @Override
        public void sendStartSpanMetaDataCallback(ErrorCode p_sendStartSpanMetaDataCallback_1_) {
        }

        @Override
        public void sendEndSpanMetaDataCallback(ErrorCode p_sendEndSpanMetaDataCallback_1_) {
        }
    };
    protected IStatCallbacks field_176006_B = new IStatCallbacks(){

        @Override
        public void statCallback(StatType p_statCallback_1_, long p_statCallback_2_) {
            switch (p_statCallback_1_) {
                case TTV_ST_RTMPSTATE: {
                    IngestServerTester.this.field_153051_i = RTMPState.lookupValue((int)p_statCallback_2_);
                    break;
                }
                case TTV_ST_RTMPDATASENT: {
                    IngestServerTester.this.field_153050_h = p_statCallback_2_;
                }
            }
        }
    };

    public void func_153042_a(IngestTestListener p_153042_1_) {
        this.field_153044_b = p_153042_1_;
    }

    public IngestServer func_153040_c() {
        return this.field_153059_q;
    }

    public int func_153028_p() {
        return this.field_153062_t;
    }

    public boolean func_153032_e() {
        return this.field_153047_e == IngestTestState.Finished || this.field_153047_e == IngestTestState.Cancelled || this.field_153047_e == IngestTestState.Failed;
    }

    public float func_153030_h() {
        return this.field_153066_x;
    }

    public IngestServerTester(Stream p_i1019_1_, IngestList p_i1019_2_) {
        this.field_153045_c = p_i1019_1_;
        this.field_153046_d = p_i1019_2_;
    }

    public void func_176004_j() {
        if (this.field_153047_e == IngestTestState.Uninitalized) {
            this.field_153062_t = 0;
            this.field_153060_r = false;
            this.field_153061_s = false;
            this.field_176009_x = false;
            this.field_176008_y = false;
            this.field_176007_z = false;
            this.field_153058_p = this.field_153045_c.getStatCallbacks();
            this.field_153045_c.setStatCallbacks(this.field_176006_B);
            this.field_153057_o = this.field_153045_c.getStreamCallbacks();
            this.field_153045_c.setStreamCallbacks(this.field_176005_A);
            this.field_153052_j = new VideoParams();
            this.field_153052_j.targetFps = 60;
            this.field_153052_j.maxKbps = 3500;
            this.field_153052_j.outputWidth = 1280;
            this.field_153052_j.outputHeight = 720;
            this.field_153052_j.pixelFormat = PixelFormat.TTV_PF_BGRA;
            this.field_153052_j.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
            this.field_153052_j.disableAdaptiveBitrate = true;
            this.field_153052_j.verticalFlip = false;
            this.field_153045_c.getDefaultParams(this.field_153052_j);
            this.audioParameters = new AudioParams();
            this.audioParameters.audioEnabled = false;
            this.audioParameters.enableMicCapture = false;
            this.audioParameters.enablePlaybackCapture = false;
            this.audioParameters.enablePassthroughAudio = false;
            this.field_153055_m = Lists.newArrayList();
            int i = 3;
            for (int j = 0; j < i; ++j) {
                FrameBuffer framebuffer = this.field_153045_c.allocateFrameBuffer(this.field_153052_j.outputWidth * this.field_153052_j.outputHeight * 4);
                if (!framebuffer.getIsValid()) {
                    this.func_153031_o();
                    this.func_153034_a(IngestTestState.Failed);
                    return;
                }
                this.field_153055_m.add(framebuffer);
                this.field_153045_c.randomizeFrameBuffer(framebuffer);
            }
            this.func_153034_a(IngestTestState.Starting);
            this.field_153054_l = System.currentTimeMillis();
        }
    }

    public void func_153041_j() {
        if (!(this.func_153032_e() || this.field_153047_e == IngestTestState.Uninitalized || this.field_176008_y || this.field_176007_z)) {
            switch (this.field_153047_e) {
                case Starting: 
                case DoneTestingServer: {
                    if (this.field_153059_q != null) {
                        if (this.field_153061_s || !this.field_153056_n) {
                            this.field_153059_q.bitrateKbps = 0.0f;
                        }
                        this.func_153035_b(this.field_153059_q);
                        break;
                    }
                    this.field_153054_l = 0L;
                    this.field_153061_s = false;
                    this.field_153056_n = true;
                    if (this.field_153047_e != IngestTestState.Starting) {
                        ++this.field_153062_t;
                    }
                    if (this.field_153062_t < this.field_153046_d.getServers().length) {
                        this.field_153059_q = this.field_153046_d.getServers()[this.field_153062_t];
                        this.func_153036_a(this.field_153059_q);
                        break;
                    }
                    this.func_153034_a(IngestTestState.Finished);
                    break;
                }
                case ConnectingToServer: 
                case TestingServer: {
                    this.func_153029_c(this.field_153059_q);
                    break;
                }
                case Cancelling: {
                    this.func_153034_a(IngestTestState.Cancelled);
                }
            }
            this.func_153038_n();
            if (this.field_153047_e == IngestTestState.Cancelled || this.field_153047_e == IngestTestState.Finished) {
                this.func_153031_o();
            }
        }
    }

    public void func_153039_l() {
        if (!this.func_153032_e() && !this.field_153060_r) {
            this.field_153060_r = true;
            if (this.field_153059_q != null) {
                this.field_153059_q.bitrateKbps = 0.0f;
            }
        }
    }

    protected boolean func_153036_a(IngestServer p_153036_1_) {
        this.field_153056_n = true;
        this.field_153050_h = 0L;
        this.field_153051_i = RTMPState.Idle;
        this.field_153059_q = p_153036_1_;
        this.field_176008_y = true;
        this.func_153034_a(IngestTestState.ConnectingToServer);
        ErrorCode errorcode = this.field_153045_c.start(this.field_153052_j, this.audioParameters, p_153036_1_, StartFlags.TTV_Start_BandwidthTest, true);
        if (ErrorCode.failed(errorcode)) {
            this.field_176008_y = false;
            this.field_153056_n = false;
            this.func_153034_a(IngestTestState.DoneTestingServer);
            return false;
        }
        this.field_153064_v = this.field_153050_h;
        p_153036_1_.bitrateKbps = 0.0f;
        this.field_153063_u = 0;
        return true;
    }

    protected void func_153035_b(IngestServer p_153035_1_) {
        if (this.field_176008_y) {
            this.field_153061_s = true;
        } else if (this.field_176009_x) {
            this.field_176007_z = true;
            ErrorCode errorcode = this.field_153045_c.stop(true);
            if (ErrorCode.failed(errorcode)) {
                this.field_176005_A.stopCallback(ErrorCode.TTV_EC_SUCCESS);
                System.out.println("Stop failed: " + errorcode.toString());
            }
            this.field_153045_c.pollStats();
        } else {
            this.field_176005_A.stopCallback(ErrorCode.TTV_EC_SUCCESS);
        }
    }

    protected long func_153037_m() {
        return System.currentTimeMillis() - this.field_153054_l;
    }

    protected void func_153038_n() {
        float f = this.func_153037_m();
        switch (this.field_153047_e) {
            case Starting: 
            case ConnectingToServer: 
            case Uninitalized: 
            case Finished: 
            case Cancelled: 
            case Failed: {
                this.field_153066_x = 0.0f;
                break;
            }
            case DoneTestingServer: {
                this.field_153066_x = 1.0f;
                break;
            }
            default: {
                this.field_153066_x = f / (float)this.field_153048_f;
            }
        }
        switch (this.field_153047_e) {
            case Finished: 
            case Cancelled: 
            case Failed: {
                this.field_153065_w = 1.0f;
                break;
            }
            default: {
                this.field_153065_w = (float)this.field_153062_t / (float)this.field_153046_d.getServers().length;
                this.field_153065_w += this.field_153066_x / (float)this.field_153046_d.getServers().length;
            }
        }
    }

    protected boolean func_153029_c(IngestServer p_153029_1_) {
        if (!this.field_153061_s && !this.field_153060_r && this.func_153037_m() < this.field_153048_f) {
            if (!this.field_176008_y && !this.field_176007_z) {
                ErrorCode errorcode = this.field_153045_c.submitVideoFrame(this.field_153055_m.get(this.field_153063_u));
                if (ErrorCode.failed(errorcode)) {
                    this.field_153056_n = false;
                    this.func_153034_a(IngestTestState.DoneTestingServer);
                    return false;
                }
                this.field_153063_u = (this.field_153063_u + 1) % this.field_153055_m.size();
                this.field_153045_c.pollStats();
                if (this.field_153051_i == RTMPState.SendVideo) {
                    this.func_153034_a(IngestTestState.TestingServer);
                    long i = this.func_153037_m();
                    if (i > 0L && this.field_153050_h > this.field_153064_v) {
                        p_153029_1_.bitrateKbps = (float)(this.field_153050_h * 8L) / (float)this.func_153037_m();
                        this.field_153064_v = this.field_153050_h;
                    }
                }
                return true;
            }
            return true;
        }
        this.func_153034_a(IngestTestState.DoneTestingServer);
        return true;
    }

    protected void func_153031_o() {
        this.field_153059_q = null;
        if (this.field_153055_m != null) {
            for (int i = 0; i < this.field_153055_m.size(); ++i) {
                this.field_153055_m.get(i).free();
            }
            this.field_153055_m = null;
        }
        if (this.field_153045_c.getStatCallbacks() == this.field_176006_B) {
            this.field_153045_c.setStatCallbacks(this.field_153058_p);
            this.field_153058_p = null;
        }
        if (this.field_153045_c.getStreamCallbacks() == this.field_176005_A) {
            this.field_153045_c.setStreamCallbacks(this.field_153057_o);
            this.field_153057_o = null;
        }
    }

    protected void func_153034_a(IngestTestState p_153034_1_) {
        if (p_153034_1_ != this.field_153047_e) {
            this.field_153047_e = p_153034_1_;
            if (this.field_153044_b != null) {
                this.field_153044_b.func_152907_a(this, p_153034_1_);
            }
        }
    }

    public static enum IngestTestState {
        Uninitalized,
        Starting,
        ConnectingToServer,
        TestingServer,
        DoneTestingServer,
        Finished,
        Cancelling,
        Cancelled,
        Failed;

    }

    public static interface IngestTestListener {
        public void func_152907_a(IngestServerTester var1, IngestTestState var2);
    }
}

