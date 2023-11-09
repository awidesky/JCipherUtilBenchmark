/*
 * ===================== Original Benchmark stub License =====================
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.github.awidesky.jCipherUtilBenchmark;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.github.awidesky.jCipherUtil.AbstractCipherUtil;
import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AESKeySize;
import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AES_GCMCipherUtil;
import io.github.awidesky.jCipherUtil.messageInterface.InPut;

/*
 * mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=io.github.awidesky.jCipherUtilBenchmark -DartifactId=BenchmarkStub -Dversion=1.0
 *
 * 
 * Step 1: build Maven project
 *  1. Right click on project,
 *  2. Select Run As,
 *  3. Select Maven Build and specify goals as clean install
 * 
 * Step 2: run tests
 * 1. Right click on project,
 * 2. Select Run As,
 * 3. Select Java Application and choose either Main - org.openjdk.jmh or the main you created
 * 
 * */

@Warmup(iterations = 1)
@Measurement(iterations = 2)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1)
public class MyBenchmark {

    private static byte[] plain = new byte[32 * 1024 * 1024];
    private static byte[] encrypted;
    
    @Setup(Level.Trial)
    public static void genData() {
    	new Random().nextBytes(plain);
    	encrypted = new AES_GCMCipherUtil.Builder(AESKeySize.SIZE_128).build("Hello, World!".toCharArray()).encryptToSingleBuffer(InPut.from(plain));
    }
    
    @Benchmark
    public void original_encrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = new AES_GCMCipherUtil.Builder(AESKeySize.SIZE_128).build("Hello, World!".toCharArray());
    	c.engine = false;
    	bh.consume(c.encryptToSingleBuffer(InPut.from(plain)));
    }
    @Benchmark
    public void original_decrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = new AES_GCMCipherUtil.Builder(AESKeySize.SIZE_128).build("Hello, World!".toCharArray());
    	c.engine = false;
    	bh.consume(c.decryptToSingleBuffer(InPut.from(encrypted)));
    }
    
    @Benchmark
    public void engine_encrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = new AES_GCMCipherUtil.Builder(AESKeySize.SIZE_128).build("Hello, World!".toCharArray());
    	c.engine = true;
    	bh.consume(c.encryptToSingleBuffer(InPut.from(plain)));
    }
    @Benchmark
    public void engine_decrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = new AES_GCMCipherUtil.Builder(AESKeySize.SIZE_128).build("Hello, World!".toCharArray());
    	c.engine = true;
    	bh.consume(c.decryptToSingleBuffer(InPut.from(encrypted)));
    }
    

}
